package sg.edu.nus.cats.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.CourseFeeClaim;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.service.ClaimService;
import sg.edu.nus.cats.utils.EmployeeAuthHelper;

// For file handling
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Controller
@RequestMapping("/claims")
public class ClaimController {

	private final ClaimService claimService;

	private final EmployeeAuthHelper employeeAuthentication;

	// Open the reimbursement claim form for one completed course application.
	@GetMapping("/new/{applicationId}")
	public String showClaimForm(
			@PathVariable("applicationId") Long applicationId,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		// Check that an employee is logged in.
		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return "redirect:/login";
		}

		try {

			// Retrieve the application and verify that:
			// - it belongs to this employee
			// - it is completed
			// - it is eligible for reimbursement
			// - it does not already have a claim
			CourseApplication application =
					claimService.findApplicationForClaim(
							applicationId,
							employee);

			// Make the application available to claim-form.html.
			model.addAttribute("courseRequest", application);

		} catch (IllegalArgumentException validationError) {

			redirectAttributes.addFlashAttribute(
					"error",
					validationError.getMessage());

			return "redirect:/applications";
		}

		return "claim-form";
	}

	// Receive the receipt and certificate submitted by the employee.
	@PostMapping("/{applicationId}")
	public String submitClaim(
			@PathVariable("applicationId") Long applicationId,
			@RequestParam("receipt") MultipartFile receipt,
			@RequestParam("certificate") MultipartFile certificate,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		// Check that an employee is logged in.
		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return "redirect:/login";
		}

		try {

			// ClaimService handles validation, document storage,
			// claim creation and cleanup if submission fails.
			claimService.submit(
					applicationId,
					employee,
					receipt,
					certificate);
		
		} catch (IllegalArgumentException validationError) {

			redirectAttributes.addFlashAttribute(
					"error",
					validationError.getMessage());

			return "redirect:/applications";
		}

		redirectAttributes.addFlashAttribute(
				"success",
				"Reimbursement claim submitted successfully");

		return "redirect:/applications";
	}
	
	// Allow an employee to securely view/download
	// the receipt belonging to their own claim.
	@GetMapping("/{claimId}/receipt")
	public ResponseEntity<Resource> viewReceipt(
			@PathVariable("claimId") Long claimId,
			HttpSession session) {

		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return ResponseEntity.status(401).build();
		}

		try {

			Path file =
					claimService.getReceiptFile(
							claimId,
							employee);

			return buildFileResponse(file);

		} catch (IllegalArgumentException | IOException error) {

			return ResponseEntity.notFound().build();
		}
	}
	
	// Allow an employee to securely view/download
	// the certificate belonging to their own claim.
	@GetMapping("/{claimId}/certificate")
	public ResponseEntity<Resource> viewCertificate(
			@PathVariable("claimId") Long claimId,
			HttpSession session) {

		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return ResponseEntity.status(401).build();
		}

		try {

			Path file =
					claimService.getCertificateFile(
							claimId,
							employee);

			return buildFileResponse(file);

		} catch (IllegalArgumentException | IOException error) {

			return ResponseEntity.notFound().build();
		}
	}
	
	// Private helper method for viewReceipt() and viewCertificate()
	private ResponseEntity<Resource> buildFileResponse(Path file)
			throws IOException {

		Resource resource =
				new UrlResource(file.toUri());

		String contentType =
				Files.probeContentType(file);

		if (contentType == null) {
			contentType =
					MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(
						HttpHeaders.CONTENT_DISPOSITION,
						"inline; filename=\"" +
								file.getFileName().toString() +
								"\"")
				.body(resource);
	}
	
	// Display the employee's reimbursement claims and
	// training budget information for the current year.
	@GetMapping
	public String showTrainingLedger(
			HttpSession session,
			Model model) {

		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return "redirect:/login";
		}

		int currentYear = LocalDate.now().getYear();

		List<CourseFeeClaim> claimHistory =
				claimService.findMyClaimsForYear(
						employee,
						currentYear);

		BigDecimal annualBudget =
				claimService.getAnnualBudget(
						employee,
						currentYear);

		BigDecimal committedFees =
				claimService.calculateCommittedFees(
						employee,
						currentYear);

		BigDecimal completedFees =
				claimService.calculateCompletedFees(
						employee,
						currentYear);

		BigDecimal reimbursedClaims =
				claimService.calculateReimbursedClaims(
						employee,
						currentYear);

		BigDecimal pendingClaims =
				claimService.calculatePendingClaims(
						employee,
						currentYear);

		BigDecimal remainingBudget =
				claimService.calculateRemainingBudget(
						employee,
						currentYear);

		model.addAttribute("currentYear", currentYear);
		model.addAttribute("claims", claimHistory);

		model.addAttribute("annualBudget", annualBudget);
		model.addAttribute("committedFees", committedFees);
		model.addAttribute("completedFees", completedFees);
		model.addAttribute("reimbursedClaims", reimbursedClaims);
		model.addAttribute("pendingClaims", pendingClaims);
		model.addAttribute("remainingBudget", remainingBudget);

		return "training-ledger";
	}
	
	// Display one reimbursement claim belonging to the logged-in employee.
	@GetMapping("/{claimId}")
	public String showClaimDetails(
			@PathVariable("claimId") Long claimId,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes) {

		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return "redirect:/login";
		}

		try {

			CourseFeeClaim claim =
					claimService.findMyClaim(
							claimId,
							employee);

			model.addAttribute("claim", claim);

		} catch (IllegalArgumentException validationError) {

			redirectAttributes.addFlashAttribute(
					"error",
					validationError.getMessage());

			return "redirect:/claims";
		}

		return "claim-detail";
	}
	
	@PostMapping("/{claimId}/reimburse")
	public String markClaimReimbursed(
			@PathVariable("claimId") Long claimId,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		Employee employee =
				employeeAuthentication.getEmployee(session);

		if (employee == null) {
			return "redirect:/login";
		}

		try {

			claimService.markReimbursed(
					claimId,
					employee);

		} catch (IllegalArgumentException validationError) {

			redirectAttributes.addFlashAttribute(
					"error",
					validationError.getMessage());

			return "redirect:/claims/" + claimId;
		}

		redirectAttributes.addFlashAttribute(
				"success",
				"Claim marked as reimbursed");

		return "redirect:/claims/" + claimId;
	}
	
}