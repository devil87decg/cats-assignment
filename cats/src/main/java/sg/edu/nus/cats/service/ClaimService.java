package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// For file handling
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.ClaimStatus;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.CourseFeeClaim;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.TrainingAllowance;
import sg.edu.nus.cats.repository.AllowanceRepository;
import sg.edu.nus.cats.repository.ApplicationRepository;
import sg.edu.nus.cats.repository.ClaimRepository;

@Service
public class ClaimService {

	private final ClaimRepository claims;
	private final ApplicationRepository applications;
	private final AllowanceRepository allowances;

	private final ApplicationService applicationService;

	public ClaimService(
			ClaimRepository claims,
			ApplicationRepository applications,
			AllowanceRepository allowances,
			ApplicationService applicationService) {

		this.claims = claims;
		this.applications = applications;
		this.allowances = allowances;
		this.applicationService = applicationService;
	}

	// --- Employee-related ---
	
	// Check whether an application is eligible for course fee reimbursement.
	public boolean canClaim(CourseApplication application) {

		if (application == null || application.getId() == null) {
			return false;
		}

		// Only completed courses can be claimed.
		if (application.getStatus() != ApplicationStatus.COMPLETED) {
			return false;
		}

		// Internal Training is not eligible for reimbursement.
		if (isInternalTraining(application)) {
			return false;
		}

		// A claim requires a positive course fee.
		if (application.getFee() == null
				|| application.getFee().compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}

		// Each application can have only one claim.
		if (claims.existsByApplicationId(application.getId())) {
			return false;
		}

		return true;
	}
	
	// Check whether this application is for Internal Training.
	private boolean isInternalTraining(CourseApplication application) {

		if (application.getCourse() != null
				&& application.getCourse().getCategory() != null) {

			return application.getCourse()
					.getCategory()
					.isInternalTraining();
		}

		String category = application.getLegacyCategory();

		if ("INTERNAL".equals(category)) {
			return true;
		}

		if ("EXTERNAL".equals(category)
				|| "CERTIFICATION".equals(category)) {
			return false;
		}

		throw new IllegalStateException(
				"Missing or unknown category for application "
						+ application.getId());
	}

	// Find an application that the employee wants to claim.
	// This is used when opening the reimbursement form.
	public CourseApplication findApplicationForClaim(
			Long applicationId,
			Employee employee) {

		CourseApplication application = applications.findById(applicationId)
				.orElseThrow(() ->
						new IllegalArgumentException("Application not found"));

		// Employee can claim only their own course application.
		if (!application.getEmployee().getId().equals(employee.getId())) {

			throw new IllegalArgumentException(
					"You can claim reimbursement only for your own application");
		}

		if (!canClaim(application)) {

			throw new IllegalArgumentException(
					"This application is not eligible for reimbursement");
		}

		return application;
	}

	// Submit a new reimbursement claim together with its supporting documents.
	@Transactional // only protects database, cannot roll back filesystem changes
	public CourseFeeClaim submit(
			Long applicationId,
			Employee employee,
			MultipartFile receipt,
			MultipartFile certificate) {

		// Validate the application before writing any files.
		// This checks ownership, completion status, course category,
		// fee and whether a claim already exists.
		CourseApplication application =
				findApplicationForClaim(applicationId, employee);

		// Validate both documents before saving either one.
		validateDocument(receipt, "Receipt");
		validateDocument(certificate, "Certificate of completion");

		String receiptPath = null;
		String certificatePath = null;

		try {

			// Save both supporting documents.
			receiptPath =
					saveDocument(receipt, "Receipt");

			certificatePath =
					saveDocument(
							certificate,
							"Certificate of completion");

			// Create the reimbursement claim.
			CourseFeeClaim claim =
					new CourseFeeClaim();

			claim.setApplication(application);

			// The amount comes from the saved course application,
			// not from employee input.
			claim.setAmount(application.getFee());

			claim.setClaimDate(LocalDateTime.now());

			claim.setReceiptPath(receiptPath);
			claim.setCertificatePath(certificatePath);

			claim.setStatus(ClaimStatus.PENDING);

			// No manager decision has been made yet.
			claim.setManagerReason(null);
			claim.setDecidedBy(null);
			claim.setDecisionDate(null);

			return claims.save(claim);

		} catch (Exception error) {

			// If submission fails after either document was saved,
			// remove any files created by this submission.
			deleteDocument(receiptPath);
			deleteDocument(certificatePath);

			if (error instanceof IllegalArgumentException validationError) {
				throw validationError;
			}

			throw new IllegalArgumentException(
					"Unable to submit reimbursement claim");
		}
	}
	
	// Save a document uploaded for a reimbursement claim.
	private String saveDocument(MultipartFile file, String documentType) {

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException(
					documentType + " is required");
		}

		try {

			// Store claim documents inside the uploads/claims folder.
			Path uploadDirectory =
					Paths.get("uploads", "claims");

			Files.createDirectories(uploadDirectory);

			// Keep the original file extension, if one exists.
			String originalFilename = file.getOriginalFilename();

			String extension = "";

			if (originalFilename != null
					&& originalFilename.contains(".")) {

				extension = originalFilename.substring(
						originalFilename.lastIndexOf("."));
			}

			// Generate our own filename so uploaded files cannot
			// overwrite one another.
			String storedFilename =
					UUID.randomUUID().toString() + extension;

			Path destination =
					uploadDirectory.resolve(storedFilename);

			file.transferTo(destination);

			// Store the relative path in the database.
			return destination.toString();

		} catch (IOException error) {

			throw new IllegalArgumentException(
					"Unable to save " + documentType);
		}
	}
	
	// Check that a required supporting document was uploaded.
	private void validateDocument(
			MultipartFile file,
			String documentType) {

		if (file == null || file.isEmpty()) {

			throw new IllegalArgumentException(
					documentType + " is required");
		}
	}
	
	// Remove a document that was created during a failed claim submission.
	private void deleteDocument(String storedPath) {

		if (storedPath == null || storedPath.isBlank()) {
			return;
		}

		try {

			Path path =
					Paths.get(storedPath);

			Files.deleteIfExists(path);

		} catch (IOException error) {

			// Do not replace the original submission error
			// just because cleanup also failed.
			System.err.println(
					"Unable to delete claim document: "
							+ storedPath);
		}
	}

	// Retrieve all reimbursement claims belonging to this employee.
	// This can be used for the Training Ledger.
	public List<CourseFeeClaim> findMyClaims(Employee employee) {

		return claims.findByApplicationEmployeeId(employee.getId());
	}
	
	// Retrieve this employee's reimbursement claims for a selected year.
	public List<CourseFeeClaim> findMyClaimsForYear(
			Employee employee,
			int year) {

		LocalDate firstDay = LocalDate.of(year, 1, 1);
		LocalDate lastDay = LocalDate.of(year, 12, 31);

		return claims
				.findByApplicationEmployeeIdAndApplicationStartDateBetween(
						employee.getId(),
						firstDay,
						lastDay);
	}

	// Retrieve one claim and make sure it belongs to this employee.
	// This can be used for claim-detail.html.
	public CourseFeeClaim findMyClaim(Long claimId, Employee employee) {

		CourseFeeClaim claim = claims.findById(claimId)
				.orElseThrow(() ->
						new IllegalArgumentException("Claim not found"));

		if (!claim.getApplication()
				.getEmployee()
				.getId()
				.equals(employee.getId())) {

			throw new IllegalArgumentException(
					"You can only view your own reimbursement claims");
		}

		return claim;
	}
	
	@Transactional
	public CourseFeeClaim markReimbursed(
			Long claimId,
			Employee employee) {

		CourseFeeClaim claim =
				findMyClaim(
						claimId,
						employee);

		if (claim.getStatus() != ClaimStatus.APPROVED) {

			throw new IllegalArgumentException(
					"Only approved claims can be marked as reimbursed");
		}

		claim.setStatus(
				ClaimStatus.REIMBURSED);

		return claims.save(claim);
	}
	
	// Retrieve the receipt file belonging to one of this employee's claims.
	public Path getReceiptFile(Long claimId, Employee employee) {

		// findMyClaim also checks that the claim belongs to this employee.
		CourseFeeClaim claim = findMyClaim(claimId, employee);

		return getDocumentPath(
				claim.getReceiptPath(),
				"Receipt");
	}
	
	// Retrieve the certificate file belonging to one of this employee's claims.
	public Path getCertificateFile(Long claimId, Employee employee) {

		// findMyClaim also checks that the claim belongs to this employee.
		CourseFeeClaim claim = findMyClaim(claimId, employee);

		return getDocumentPath(
				claim.getCertificatePath(),
				"Certificate of completion");
	}

	// Validate that the stored document still exists.
	private Path getDocumentPath(
			String storedPath,
			String documentType) {

		if (storedPath == null || storedPath.isBlank()) {

			throw new IllegalArgumentException(
					documentType + " not found");
		}

		Path uploadDirectory =
				Paths.get("uploads", "claims")
						.toAbsolutePath()
						.normalize();

		Path path =
				Paths.get(storedPath)
						.toAbsolutePath()
						.normalize();

		// Do not allow access to files outside the claim upload directory.
		if (!path.startsWith(uploadDirectory)) {

			throw new IllegalArgumentException(
					"Invalid document path");
		}

		if (!Files.exists(path)
				|| !Files.isRegularFile(path)) {

			throw new IllegalArgumentException(
					documentType + " not found");
		}

		return path;
	}
	
	// --- Training Ledger ---
	
	// Retrieve the employee's training budget for the selected year.
	public BigDecimal getAnnualBudget(Employee employee, int year) {

		Optional<TrainingAllowance> allowance =
				allowances.findByEmployeeIdAndYear(
						employee.getId(),
						year);

		if (allowance.isEmpty()
				|| allowance.get().getFeeBudget() == null) {

			return BigDecimal.ZERO;
		}

		return allowance.get().getFeeBudget();
	}
	
	public BigDecimal calculateCommittedFees(
			Employee employee,
			int year) {

		return applicationService.calculateUsedFees(
				employee,
				year);
	}
	
	public BigDecimal calculateCompletedFees(
			Employee employee,
			int year) {

		LocalDate firstDay = LocalDate.of(year, 1, 1);
		LocalDate lastDay = LocalDate.of(year, 12, 31);

		return applicationService.calculateCompletedFees(
				employee.getId(),
				firstDay,
				lastDay);
	}
	
	public BigDecimal calculatePendingClaims(
			Employee employee,
			int year) {

		BigDecimal total = BigDecimal.ZERO;

		List<CourseFeeClaim> yearlyClaims =
				findMyClaimsForYear(employee, year);

		for (CourseFeeClaim claim : yearlyClaims) {

			if (claim.getStatus() == ClaimStatus.PENDING) {
				total = total.add(claim.getAmount());
			}
		}

		return total;
	}
	
	public BigDecimal calculateReimbursedClaims(
			Employee employee,
			int year) {

		BigDecimal total = BigDecimal.ZERO;

		List<CourseFeeClaim> yearlyClaims =
				findMyClaimsForYear(employee, year);

		for (CourseFeeClaim claim : yearlyClaims) {

			if (claim.getStatus() == ClaimStatus.REIMBURSED) {
				total = total.add(claim.getAmount());
			}
		}

		return total;
	}
	
	public BigDecimal calculateRemainingBudget(
			Employee employee,
			int year) {

		return applicationService.balanceBudget(
				employee,
				year);
	}
	
	// --- Manager-related ---
	
	// Retrieve pending reimbursement claims belonging
	// to this manager's direct subordinates.
	public List<CourseFeeClaim> findPendingClaims(
			Employee manager) {

		return claims
				.findByApplicationEmployeeSupervisorIdAndStatusOrderByClaimDateAsc(
						manager.getId(),
						ClaimStatus.PENDING);
	}
	
	// Retrieve one reimbursement claim for manager review.
	public CourseFeeClaim findClaimForReview(
			Long claimId,
			Employee manager) {

		if (manager.getUser().getRole() != Role.MANAGER) {

			throw new IllegalArgumentException(
					"Manager role is required to review claims");
		}

		CourseFeeClaim claim =
				claims.findById(claimId)
						.orElseThrow(() ->
								new IllegalArgumentException(
										"Claim not found"));

		Employee claimant =
				claim.getApplication()
						.getEmployee();

		Employee supervisor =
				claimant.getSupervisor();

		if (supervisor == null) {

			throw new IllegalArgumentException(
					"The employee has no supervisor assigned");
		}

		if (!supervisor.getId().equals(manager.getId())) {

			throw new IllegalArgumentException(
					"You can only review claims from your own subordinates");
		}

		return claim;
	}
	
	@Transactional
	public CourseFeeClaim decide(
			Long claimId,
			Employee manager,
			boolean approve,
			String reason) {

		CourseFeeClaim claim =
				findClaimForReview(
						claimId,
						manager);

		// Only claims awaiting a manager decision may be decided.
		if (claim.getStatus() != ClaimStatus.PENDING) {

			throw new IllegalArgumentException(
					"Only pending claims can be approved or rejected");
		}

		// A reason is mandatory for both approval and rejection.
		if (reason == null || reason.isBlank()) {

			throw new IllegalArgumentException(
					"A reason is required for approval or rejection");
		}

		claim.setManagerReason(reason);

		claim.setDecidedBy(manager);

		claim.setDecisionDate(
				LocalDateTime.now());

		if (approve) {

			claim.setStatus(
					ClaimStatus.APPROVED);

		} else {

			claim.setStatus(
					ClaimStatus.REJECTED);
		}

		return claims.save(claim);
	}
	
	// Retrieve a subordinate's receipt for manager review.
	public Path getReceiptFileForReview(
			Long claimId,
			Employee manager) {

		// findClaimForReview checks that the claimant
		// is supervised by this manager.
		CourseFeeClaim claim =
				findClaimForReview(
						claimId,
						manager);

		return getDocumentPath(
				claim.getReceiptPath(),
				"Receipt");
	}

	// Retrieve a subordinate's certificate for manager review.
	public Path getCertificateFileForReview(
			Long claimId,
			Employee manager) {

		// findClaimForReview checks that the claimant
		// is supervised by this manager.
		CourseFeeClaim claim =
				findClaimForReview(
						claimId,
						manager);

		return getDocumentPath(
				claim.getCertificatePath(),
				"Certificate of completion");
	}
	
	// Retrieve all reimbursement claims belonging to an employee.
	public List<CourseFeeClaim> findEmployeeClaimHistory(
	        Employee employee) {

	    return claims
	            .findByApplicationEmployeeIdOrderByClaimDateDesc(
	                    employee.getId());
	}
	
	// Retrieve reimbursement claims for courses in a selected year.
	public List<CourseFeeClaim> findEmployeeClaimsForYear(
	        Employee employee,
	        int year) {

	    LocalDate firstDay =
	            LocalDate.of(year, 1, 1);

	    LocalDate lastDay =
	            LocalDate.of(year, 12, 31);

	    return claims
	            .findByApplicationEmployeeIdAndApplicationStartDateBetweenOrderByClaimDateDesc(
	                    employee.getId(),
	                    firstDay,
	                    lastDay);
	}
	
}