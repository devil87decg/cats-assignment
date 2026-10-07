package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.TrainingAllowance;
import sg.edu.nus.cats.repository.AllowanceRepository;
import sg.edu.nus.cats.repository.ApplicationRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.HolidayRepository;

@Service
public class ApplicationService {

	// ApplicationService will ask dayService to count the working days in a
	// proposed course.
	private final TrainingDayService dayService;

	// Requirement as both course start date and end date must be working days
	// ApplicationService can ask holidays.existsByDate() for each end-point
	private final HolidayRepository holidays;

	// ApplicationService will use allowances to find the applicant's training-day
	// limit and fee budget for the course year
	private final AllowanceRepository allowances;

	// applications will let service look at employee's existing course requests
	// for;
	// check whether a new course overlaps another active application
	// check whether employee has already used part of their yearly allowance
	private final ApplicationRepository applications;
	private final EmployeeRepository employees;
	private final EmailService emailService;

	public ApplicationService(TrainingDayService dayService, HolidayRepository holidays, AllowanceRepository allowances,
			ApplicationRepository applications, EmployeeRepository employees, EmailService emailService) {

		this.dayService = dayService;
		this.holidays = holidays;
		this.allowances = allowances;
		this.applications = applications;
		this.employees = employees;
		this.emailService = emailService;
	}

	// Type -> CourseApplication, Parameter -> application
	private void validate(CourseApplication application, Employee applicant, Long editingId) {

		// getCourseTitle() -> reads title from application
		// == null checks whether no title was provided
		// isBlank() checks for empty text/spaces only
		if (application.getCourseTitle() == null || application.getCourseTitle().isBlank()) {

			throw new IllegalArgumentException("Course title is required");
		}

		// returns null, none was selected
		if (application.getCourse() == null) {

			throw new IllegalArgumentException("Course is required");
		}

		if (isInternalTraining(application)) {
			application.setFee(BigDecimal.ZERO);
		}

		// Reject the application if either the start date or end date is missing
		if (application.getStartDate() == null || application.getEndDate() == null) {

			throw new IllegalArgumentException("Start and end dates are required");
		}

		// rejects a missing justification, an empty string, or text made only of spaces
		if (application.getJustification() == null || application.getJustification().isBlank()) {

			throw new IllegalArgumentException("Justification is required");
		}

		// LocalDate.now() get today's date
		// isAfter(today) is true if start date is tomorrow or later
		// ! reverses it: reject a start date that is today or earlier
		LocalDate today = LocalDate.now();

		if (!application.getStartDate().isAfter(today)) {

			throw new IllegalArgumentException("Course must start on a future date");
		}

		// if end date is earlier than start date, stop and report an error
		if (application.getEndDate().isBefore(application.getStartDate())) {

			throw new IllegalArgumentException("End date must not be before start date");
		}

		// getYear() reads the year from each date
		// != "different" -> a course from Dec 26 to Jan 27 will be rejected
		if (application.getStartDate().getYear() != application.getEndDate().getYear()) {
			throw new IllegalArgumentException("Course must start and end in the same year");
		}

		// if start date or end date is not working day -> reject application
		if (!isWorkingDay(application.getStartDate()) || !isWorkingDay(application.getEndDate())) {

			throw new IllegalArgumentException("Course start and end dates must be working days");
		}

		// sends the start date and end date to the calculator built in
		// TrainingDayService
		BigDecimal days = dayService.count(application.getStartDate(), application.getEndDate());

		// rejects a course period with no working days
		if (days.compareTo(BigDecimal.ZERO) == 0) {

			throw new IllegalArgumentException("Course has no working days");
		}

		// get the fixed duration configuration for the selected course
		BigDecimal courseDuration = application.getCourse().getDurationDays();

		if (courseDuration == null) {
			throw new IllegalArgumentException("Selected course does not have a duration");
		}

		// checks half-day duration is allowed for internal training only
		if (courseDuration.compareTo(new BigDecimal("0.5")) == 0) {
			if (!isInternalTraining(application)) {
				throw new IllegalArgumentException("Half-day duration is allowed for Internal Training only");
			}

			// a half-day course must take place on one working date
			if (days.compareTo(BigDecimal.ONE) != 0) {
				throw new IllegalArgumentException("A half-day course must be scheduled on one working day");
			}

			days = courseDuration;
		} else if (days.compareTo(courseDuration) != 0) {
			throw new IllegalArgumentException("Selected course period must match the course duration of "
					+ courseDuration.stripTrailingZeros().toPlainString() + " training day(s)");
		}

		// store the calculated training days on the course application
		// if course contains three working days, application will now hold durationDays
		// = 3
		application.setDurationDays(days);

		// allowanceResult is created as a variable
		// Optional -> lookup might find an allowance or find none
		// allowances.findByEmployeeIdAndYear -> ask AllowanceRepository to search for a
		// row matching an employee ID and a year
		Optional<TrainingAllowance> allowanceResult = allowances.findByEmployeeIdAndYear(applicant.getId(),
				application.getStartDate().getYear());

		// returns true if there is no TrainingAllowance
		// throw stops the application from being accepted without a recorded limit
		if (allowanceResult.isEmpty()) {

			throw new IllegalArgumentException("Set the applicant's annual training allowance first");
		}

		TrainingAllowance limit = allowanceResult.get();

		if (limit.getDayLimit() == null) {

			throw new IllegalArgumentException("Set the applicant's training-day limit first");
		}

		// get the year needed for allowance calculation
		int courseYear = application.getStartDate().getYear();
		// define the dates that cover courseYear
		LocalDate firstDay = LocalDate.of(courseYear, 1, 1);
		LocalDate lastDay = LocalDate.of(courseYear, 12, 31);

		// existingApplications holds the matching course requests; it may also be an
		// empty list
		// retrieve lists of records
		List<CourseApplication> existingApplications = applications
				.findByEmployeeIdAndStartDateBetween(applicant.getId(), firstDay, lastDay);

		// Start the count of previously requested training days at zero
		BigDecimal usedDays = BigDecimal.ZERO;
		// Start the fees from zero
		BigDecimal usedFees = BigDecimal.ZERO;
		// For each application in the list, call the current one existing
		for (CourseApplication existing : existingApplications) {

			// if an applicant is editing an application, and this is that same application,
			// skip it
			if (editingId != null && editingId.equals(existing.getId())) {
				continue;
			}

			// if a request was rejected, deleted, or cancelled, move on to the next saved
			// application without adding its days
			if (existing.getStatus() == ApplicationStatus.REJECTED || existing.getStatus() == ApplicationStatus.DELETED
					|| existing.getStatus() == ApplicationStatus.CANCELLED) {
				continue;
			}

			// if usedDays is 2 and this existing application uses 3 days, the new usedDays
			// becomes 5
			usedDays = usedDays.add(existing.getDurationDays());

			// adds the fee when the saved application is an external course or
			// certification
			if (!isInternalTraining(existing) && existing.getFee() != null) {

				usedFees = usedFees.add(existing.getFee());
			}
		}

		// usedDays.add(days) -> combines previously counted days with the new course's
		// days
		// .compareTo(...) > 0 -> means that combined amount is greater than the allowed
		// limit
		// throw rejects the application if it goes over
		if (usedDays.add(days).compareTo(limit.getDayLimit()) > 0) {

			throw new IllegalArgumentException("Course exceeds the annual training-day limit");
		}

		// if form leaves fee blank, getFee() returns null -> set it to 0
		// if the fee is not null, leave the entered amount unchanged
		if (application.getFee() == null) {

			application.setFee(BigDecimal.ZERO);
		}

		// rejects a negative course fee
		if (application.getFee().compareTo(BigDecimal.ZERO) < 0) {

			throw new IllegalArgumentException("Course fee cannot be negative");
		}

		// for an external course or certification, CATS checks whether the employee has
		// a fee budget recorded for that year
		if (!isInternalTraining(application) && application.getFee().signum() > 0 && limit.getFeeBudget() == null) {
			throw new IllegalArgumentException("Set the applicant's annual fee budget first");

		}

		// if this is a paid external course/certification, and adding its fee would
		// exceed the annual budget,
		// reject it
		if (!isInternalTraining(application) && application.getFee().signum() > 0
				&& usedFees.add(application.getFee()).compareTo(limit.getFeeBudget()) > 0) {
			throw new IllegalArgumentException("Course fees exceeds the annual fee budget");
		}

		// asks the repository for all saved course applications belonging to this
		// applicant
		List<CourseApplication> allApplications = applications.findByEmployeeId(applicant.getId());

		// look at each saved application for this employee, one at a time
		// current saved record is called existing
		for (CourseApplication existing : allApplications) {

			if (editingId != null && editingId.equals(existing.getId())) {
				continue;
			}

			// Only applied, updated, and approved applications go on to the date-clash
			// check
			if (existing.getStatus() != ApplicationStatus.APPLIED && existing.getStatus() != ApplicationStatus.UPDATED
					&& existing.getStatus() != ApplicationStatus.APPROVED) {
				continue;
			}

			// The existing course does not end before the new course starts,
			// and the existing course does not start after the new course ends
			// if both are true -> the periods share at least one date -> overlaps -> true
			boolean overlaps = !existing.getEndDate().isBefore(application.getStartDate())
					&& !existing.getStartDate().isAfter(application.getEndDate());

			// overlaps compares the dates; this if rejects a clash
			if (overlaps) {

				throw new IllegalArgumentException("Course period overlaps another application");
			}

		}

	}

	// Link the request to the applicant
	// Give a new request its starting status, APPLIED
	// Run the checks built. null means this is new, so there is no application ID
	// to skip
	// if validation passes, save it and return the saved application. if error ->
	// save(...) is not reached
	// submit is for creating a new application
	@Transactional
	public CourseApplication submit(CourseApplication application, Employee applicant) {

		// if it already has an ID, this method rejects it instead of treating it as a
		// new request
		if (application.getId() != null) {
			throw new IllegalArgumentException("New application must not have an ID");
		}

		// Retrieve the applicant from inside the transaction for Hibernate to manage.
		Employee managedApplicant = employees.findById(applicant.getId())
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		Employee manager = managedApplicant.getSupervisor();

		if (manager == null || manager.getEmail() == null || manager.getEmail().isBlank()) {

			throw new IllegalStateException("Employee's manager email address cannot be found");
		}

		application.setEmployee(managedApplicant);
		application.setStatus(ApplicationStatus.APPLIED);

		// each line clears one manager-decision field on a newly submitted application
		// No manager has approved/rejected the request yet, so "decided by" is empty
		// No decision has happened yet, so there is no decision date and time
		// No manager has approved/rejected the request yet, so reason is empty
		application.setDecidedBy(null);
		application.setDecisionDate(null);
		application.setManagerReason(null);

		// clear post-course experience comment on a new submission
		application.setExperienceComment(null);

		validate(application, managedApplicant, null);
		emailService.notifyManagerOfSubmission(manager.getEmail(), manager.getName(), managedApplicant.getName());
		return applications.save(application);

	}

	// id identifies the saved application to edit
	// changes hold the proposed new course details
	// applicant is the employee attempting to edit
	// findById(id) looks up the saved application
	// orElseThrow(...) reports an error if that ID does not exist
	public CourseApplication update(Long id, CourseApplication changes, Employee applicant) {

		CourseApplication existing = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		// existing.getEmployee().getId() -> employee ID stored on the saved application
		// applicant.getId() -> is the ID of the employee trying to edit it
		if (!existing.getEmployee().getId().equals(applicant.getId())) {
			throw new IllegalArgumentException("You can edit only your own application");
		}

		// APPLIED or UPDATED → continue editing. Any other status → reject editing
		if (existing.getStatus() != ApplicationStatus.APPLIED && existing.getStatus() != ApplicationStatus.UPDATED) {
			throw new IllegalArgumentException("Only pending application can be edited");
		}

		// check the employee's proposed edits before putting them onto the saved
		// application
		validate(changes, applicant, id);

		existing.setCourse(changes.getCourse());
		existing.setCourseTitle(changes.getCourseTitle());
		existing.setTrainingProvider(changes.getTrainingProvider());
		existing.setStartDate(changes.getStartDate());
		existing.setEndDate(changes.getEndDate());
		existing.setFee(changes.getFee());
		existing.setJustification(changes.getJustification());
		existing.setWorkDissemination(changes.getWorkDissemination());
		existing.setDurationDays(changes.getDurationDays());

		// changes the application's status from APPLIED to UPDATED after its proposed
		// changes have passed validation
		existing.setStatus(ApplicationStatus.UPDATED);

		return applications.save(existing);
	}

	// receives the application's id and the employee requesting the deletion
	public CourseApplication delete(Long id, Employee applicant) {

		CourseApplication existing = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		// compares the saved application's employee ID with the requester's employee
		// ID. if differ -> deletion is rejected
		if (!existing.getEmployee().getId().equals(applicant.getId())) {

			throw new IllegalArgumentException("You can delete only your own application");
		}
		// if either status is false, java skips the error and continues toward marking
		// it DELETED
		if (existing.getStatus() != ApplicationStatus.APPLIED && existing.getStatus() != ApplicationStatus.UPDATED) {
			throw new IllegalArgumentException("Only pending applications can be deleted");
		}

		// set status to delete
		existing.setStatus(ApplicationStatus.DELETED);

		return applications.save(existing);
	}

	public CourseApplication cancel(Long id, Employee applicant) {

		CourseApplication existing = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		if (!existing.getEmployee().getId().equals(applicant.getId())) {

			throw new IllegalArgumentException("You can cancel only your own application");
		}

		// if its status is not APPROVED, reject this cancellation
		if (existing.getStatus() != ApplicationStatus.APPROVED) {

			throw new IllegalArgumentException("Only approved applications can be cancelled");
		}

		existing.setStatus(ApplicationStatus.CANCELLED);

		return applications.save(existing);
	}

	public CourseApplication complete(Long id, Employee applicant, String experienceComment) {

		CourseApplication existing = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		if (!existing.getEmployee().getId().equals(applicant.getId())) {

			throw new IllegalArgumentException("You can complete only your own application");
		}

		// check that the course application was approved
		if (existing.getStatus() != ApplicationStatus.APPROVED) {

			throw new IllegalArgumentException("Only approved applications can be completed");
		}

		// check that the course has already ended -> is true only if that date is
		// earlier than today
		if (!existing.getEndDate().isBefore(LocalDate.now())) {

			throw new IllegalArgumentException("Course must have ended before it can be completed");
		}

		// checks if experience comment has been filled in
		if (experienceComment == null || experienceComment.isBlank()) {

			throw new IllegalArgumentException("Experience comment is requred");
		}

		// setter store the employee's comment into the saved application object
		existing.setExperienceComment(experienceComment);

		// records that the course has been completed
		existing.setStatus(ApplicationStatus.COMPLETED);

		return applications.save(existing);

	}

	@Transactional
	public CourseApplication decide(Long id, Employee manager, boolean approve, String reason) {

		Employee managedManager = employees.findById(manager.getId())
				.orElseThrow(() -> new IllegalArgumentException("Manager not found"));

		CourseApplication existing = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		// gets the applicant supervisor
		Employee applicant = existing.getEmployee();
		Employee supervisor = applicant.getSupervisor();

		// reject if applicant has no recorded supervisor or;
		// the recorded supervisor's ID does not match the ID of the employee
		if (supervisor == null || !supervisor.getId().equals(managedManager.getId())) {

			throw new IllegalArgumentException("Only the applicant's supervisor can decide this application");
		}

		// check decision marker's account has the MANAGER role
		if (managedManager.getUser().getRole() != Role.MANAGER) {

			throw new IllegalArgumentException("Manager role is required to decide applications");
		}

		// ensure that the application is still awaiting a decision
		if (existing.getStatus() != ApplicationStatus.APPLIED && existing.getStatus() != ApplicationStatus.UPDATED) {

			throw new IllegalArgumentException("Only pending applications can be approved or rejected");
		}

		if (reason == null || reason.isBlank()) {

			throw new IllegalArgumentException("A reason is required for approval or rejection");
		}

		// stores the explanation
		existing.setManagerReason(reason);
		// links the application to the employee who made the decision
		existing.setDecidedBy(managedManager);
		// record when the manager made the decision
		existing.setDecisionDate(LocalDateTime.now());

		if (approve) {

			existing.setStatus(ApplicationStatus.APPROVED);

		} else {

			existing.setStatus(ApplicationStatus.REJECTED);
		}
		CourseApplication saved = applications.save(existing);

		emailService.notifyEmployeeOfDecision(saved.getStatus(), saved.getManagerReason(), applicant.getName(),
				applicant.getEmail());
		return saved;
	}

	// returns true only if
	// !weekend: date is not Sat or Sun
	// !holdiays.existsByDate -> holiday repository did not find that date in the
	// public holiday table
	private boolean isWorkingDay(LocalDate date) {

		boolean weekend = date.getDayOfWeek().getValue() >= 6;
		return !weekend && !holidays.existsByDate(date);
	}

	// Find this employee's applications within the supplied date range
	public List<CourseApplication> findMyApplications(Long employeeId, LocalDate firstDay, LocalDate lastDay) {

		return applications.findByEmployeeIdAndStartDateBetween(employeeId, firstDay, lastDay);

	}
	
	public Page<CourseApplication> findMyApplicationsPaginated(
	        Long employeeId,
	        LocalDate firstDay,
	        LocalDate lastDay,
	        int pageNo,
	        int pageSize) {
		
	    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

	    return applications.findByEmployeeIdAndStartDateBetween(
	            employeeId,
	            firstDay,
	            lastDay,
	            pageable);
	}

	// Retrieve all saved course applications belonging to this employee
	public List<CourseApplication> findEmployeeHistory(Employee employee) {

		return applications.findByEmployeeId(employee.getId());
	}
	
	public Page<CourseApplication> findEmployeeHistoryPaginated(
	        Employee employee,
	        int pageNo,
	        int pageSize) {

	    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

	    return applications.findByEmployeeId(
	            employee.getId(),
	            pageable);
	}

	public CourseApplication findMyApplication(Long id, Employee applicant) {

		// applications.findById(id) -> ask the repository to find the application with
		// this ID
		CourseApplication courseRequest = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		// prevent an employee from viewing another employee's application
		if (!courseRequest.getEmployee().getId().equals(applicant.getId())) {

			throw new IllegalArgumentException("You can only view your own applications");
		}

		return courseRequest;
	}

	// Find pending applications belonging to this manager's subordinates, arranged
	// by employee ID
	public List<CourseApplication> findPendingApplications(Employee manager) {

		return applications.findByEmployeeSupervisorIdAndStatusInOrderByEmployeeIdAsc(manager.getId(),
				List.of(ApplicationStatus.APPLIED, ApplicationStatus.UPDATED));
	}

	// Find an application requested for manager review
	public CourseApplication findApplicationForReview(Long id, Employee manager) {

		CourseApplication courseRequest = applications.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		// Read the supervisor linked to the employee who submitted this application
		Employee supervisor = courseRequest.getEmployee().getSupervisor();

		if (supervisor == null) {

			throw new IllegalArgumentException("The applicant has no supervisor assigned");
		}

		// Allow the manager to review only their own subordinate's applications
		if (!supervisor.getId().equals(manager.getId())) {

			throw new IllegalArgumentException("You can only review your own subordinate's applications");
		}

		return courseRequest;
	}

	// Find other subordinates' approved courses overlapping this request
	public List<CourseApplication> findOtherApprovedApplications(CourseApplication courseRequest, Employee manager) {

		// Collect the approved applications that match our checks
		List<CourseApplication> overlappingApplications = new ArrayList<>();

		// Retrieve approved applications belonging to this manager's subordinates
		List<CourseApplication> approvedApplications = applications.findByEmployeeSupervisorIdAndStatus(manager.getId(),
				ApplicationStatus.APPROVED);

		// check each approved application one at a time
		for (CourseApplication existing : approvedApplications) {

			// skip applications belonging to the employee currently being reviewed
			if (existing.getEmployee().getId().equals(courseRequest.getEmployee().getId())) {

				continue;
			}

			// check whether the two courses have at least one date that overlaps
			boolean overlaps = !existing.getEndDate().isBefore(courseRequest.getStartDate())
					&& !existing.getStartDate().isAfter(courseRequest.getEndDate());

			// add this approved application if the dates overlap
			if (overlaps) {

				overlappingApplications.add(existing);
			}

		}

		return overlappingApplications;
	}

	// Calculate an employee's training-day usage for the selected year
	public BigDecimal calculateUsedDays(Employee applicant, int year) {

		BigDecimal usedDays = BigDecimal.ZERO;

		// Define the date range for the selected year.
		LocalDate firstDay = LocalDate.of(year, 1, 1);
		LocalDate lastDay = LocalDate.of(year, 12, 31);

		// Find this employee's applications starting in the selected year
		List<CourseApplication> yearlyApplications = applications.findByEmployeeIdAndStartDateBetween(applicant.getId(),
				firstDay, lastDay);

		// Check each application retrieved for the selected year
		for (CourseApplication existing : yearlyApplications) {

			// Count pending, approved, and completed applications towards the allowance
			if (existing.getStatus() == ApplicationStatus.APPLIED || existing.getStatus() == ApplicationStatus.UPDATED
					|| existing.getStatus() == ApplicationStatus.APPROVED
					|| existing.getStatus() == ApplicationStatus.COMPLETED) {

				// Add this application's training days to the running total
				usedDays = usedDays.add(existing.getDurationDays());

			}

		}

		return usedDays;
	}

	// Calculate an employee's remaining training days for the selected year
	public BigDecimal balanceDays(Employee emp, int year) {

		Long employeeId = emp.getId();

		Optional<TrainingAllowance> dayLimit = allowances.findByEmployeeIdAndYear(employeeId, year);
		BigDecimal maxDays = dayLimit.map(TrainingAllowance::getDayLimit).orElse(BigDecimal.ZERO);

		return maxDays.subtract(calculateUsedDays(emp, year));
	}

	// Count training days from completed courses within the supplied year
	public BigDecimal calculateCompletedDays(Long employeeId, LocalDate firstDay, LocalDate lastDay) {

		BigDecimal completedDays = BigDecimal.ZERO;

		List<CourseApplication> yearlyApplications = applications.findByEmployeeIdAndStartDateBetween(employeeId,
				firstDay, lastDay);

		// checks each retrieved application
		for (CourseApplication application : yearlyApplications) {

			// includes only courses with status marked completed
			if (application.getStatus() == ApplicationStatus.COMPLETED) {

				// add that course's training days to the total
				completedDays = completedDays.add(application.getDurationDays());
			}
		}

		return completedDays;
	}

	// Calculate total external course and certification fees for completed courses
	public BigDecimal calculateCompletedFees(Long employeeId, LocalDate firstDay, LocalDate lastDay) {

		BigDecimal completedFees = BigDecimal.ZERO;

		List<CourseApplication> yearlyApplications = applications.findByEmployeeIdAndStartDateBetween(employeeId,
				firstDay, lastDay);

		for (CourseApplication application : yearlyApplications) {

			// status == completed -> only include status that are completed
			// category != INTERNAL -> include external courses and certifications
			// fee != null -> check that a fee value exists before adding it
			if (application.getStatus() == ApplicationStatus.COMPLETED && !isInternalTraining(application)
					&& application.getFee() != null) {

				completedFees = completedFees.add(application.getFee());
			}
		}

		return completedFees;

	}

	// Calculate an employee's reserved and used course fees for a year
	public BigDecimal calculateUsedFees(Employee applicant, int year) {

		BigDecimal usedFees = BigDecimal.ZERO;

		// Define the date range for the selected year
		LocalDate firstDay = LocalDate.of(year, 1, 1);
		LocalDate lastDay = LocalDate.of(year, 12, 31);

		// Retrieve this employee's applications starting in that year
		List<CourseApplication> yearlyApplications = applications.findByEmployeeIdAndStartDateBetween(applicant.getId(),
				firstDay, lastDay);

		// Check each application retrieved for the selected year
		for (CourseApplication existing : yearlyApplications) {

			// Include applications that reserve or use the annual budget
			if (existing.getStatus() == ApplicationStatus.APPLIED || existing.getStatus() == ApplicationStatus.UPDATED
					|| existing.getStatus() == ApplicationStatus.APPROVED
					|| existing.getStatus() == ApplicationStatus.COMPLETED) {

				// Count external course and certification fees when a fee is recorded
				if (!isInternalTraining(existing) && existing.getFee() != null) {

					usedFees = usedFees.add(existing.getFee());
				}
			}

		}

		return usedFees;
	}

	// Calculate an employee's remaining budget for the selected year
	public BigDecimal balanceBudget(Employee emp, int year) {

		Long employeeId = emp.getId();

		Optional<TrainingAllowance> feeBudget = allowances.findByEmployeeIdAndYear(employeeId, year);
		BigDecimal maxBudget = feeBudget.map(TrainingAllowance::getFeeBudget).orElse(BigDecimal.ZERO);

		return maxBudget.subtract(calculateUsedFees(emp, year));

	}

	private boolean isInternalTraining(CourseApplication application) {

		if (application.getCourse() != null 
				&& application.getCourse().getCategory() != null) {
			
			return application.getCourse()
					.getCategory()
					.isInternalTraining();
			
		}
		
		String category = application.getLegacyCategory();
		
		if ("INTERNAL".equals(category) ) {
			
			return true;
		}
		
		if ("EXTERNAL".equals(category)
				|| "CERTIFICATION".equals(category)) {
			
			return false;
		}
		
		throw new IllegalStateException(
				"Missing or unknown category for application" + application.getId());
			
	}

}
