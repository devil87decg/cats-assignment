package sg.edu.nus.cats.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.Course;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.service.ApplicationService;
import sg.edu.nus.cats.service.ClaimService;
import sg.edu.nus.cats.service.CourseService;
import sg.edu.nus.cats.service.TrainingDayService;
import sg.edu.nus.cats.utils.EmployeeAuthHelper;

@AllArgsConstructor
@Controller
@RequestMapping("/applications")
public class ApplicationController {

	// Keep a reference to the service that validates and manages course
	// applications.
	private final ApplicationService applicationService;
	
	private final ClaimService claimService;

	private final CourseService courseService;

	private final TrainingDayService trainingDayService;

	private final EmployeeAuthHelper employeeAuthentication;

	// open the form for submitting a new course application
	@GetMapping("/new")
	public String showApplicationForm(@RequestParam(required = false) Long courseId,
			@RequestParam(required = false) LocalDate startDate, HttpSession session, Model model) {

		// check if employee have login
		if (employeeAuthentication.getEmployee(session) == null) {
			return "redirect:/login";
		}

		// Create an empty application object for the form fields
		CourseApplication courseForm = new CourseApplication();

		if (courseId == null || startDate == null) {
			session.removeAttribute("calendarCourseId");
			session.removeAttribute("calendarStartDate");
		}

		// If employee came from the Training Calendar,
		// pre-populate the selected course and training dates.
		if (courseId != null && startDate != null) {

			try {

				Course selectedCourse = courseService.findById(courseId);

				if (!selectedCourse.isActive()) {
					throw new IllegalArgumentException("Selected course is no longer available");
				}

				// Calendar applications must still be for a future course.
				if (!startDate.isAfter(LocalDate.now())) {
					throw new IllegalArgumentException("Course must start on a future date");
				}

				courseForm.setCourse(selectedCourse);
				courseForm.setStartDate(startDate);

				BigDecimal duration = selectedCourse.getDurationDays();

				LocalDate endDate = trainingDayService.calculateEndDate(startDate, duration);

				courseForm.setEndDate(endDate);
				model.addAttribute("fromCalendar", true);

				// Remember the actual calendar session on the server.
				session.setAttribute("calendarCourseId", selectedCourse.getId());
				session.setAttribute("calendarStartDate", startDate);

			} catch (IllegalArgumentException validationError) {

				model.addAttribute("error", validationError.getMessage());
			}
		}
		if (!model.containsAttribute("fromCalendar")) {
			model.addAttribute("fromCalendar", false);
		}

		// make the application object available to the HTML form
		model.addAttribute("courseForm", courseForm);

		model.addAttribute("courses", courseService.findActive());

		return "application-form";
	}

	@GetMapping("/calculate-end-date")
	@ResponseBody
	public ResponseEntity<String> calculateEndDate(@RequestParam Long courseId, @RequestParam LocalDate startDate) {
		try {

			// Find the course selected by the employee.
			Course selectedCourse = courseService.findById(courseId);

			// Do not calculate dates for a course that has been deactivated.
			if (!selectedCourse.isActive()) {
				throw new IllegalArgumentException("Selected course is no longer available");
			}

			// Read the fixed duration from the Course Catalogue.
			BigDecimal duration = selectedCourse.getDurationDays();

			// Calculate the end date while excluding weekends and public holidays.
			LocalDate endDate = trainingDayService.calculateEndDate(startDate, duration);

			return ResponseEntity.ok(endDate.toString());

		} catch (IllegalArgumentException validationError) {

			return ResponseEntity.badRequest().body(validationError.getMessage());
		}
	}

	// Receive the course details submitted by the application form
	@PostMapping
	public String submitApplication(@ModelAttribute("courseForm") CourseApplication courseForm,
			@RequestParam Long courseId, @RequestParam(defaultValue = "false") boolean fromCalendar,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		try {
			Course selectedCourse = courseService.findById(courseId);

			if (!selectedCourse.isActive()) {
				throw new IllegalArgumentException("Selected course is no longer available");
			}

			if (fromCalendar) {

				Long calendarCourseId = (Long) session.getAttribute("calendarCourseId");

				LocalDate calendarStartDate = (LocalDate) session.getAttribute("calendarStartDate");

				if (calendarCourseId == null || calendarStartDate == null) {
					throw new IllegalArgumentException(
							"Training calendar session is no longer available. Please select the course again.");
				}

				if (!calendarCourseId.equals(courseId)) {
					throw new IllegalArgumentException(
							"The selected course does not match the training calendar session.");
				}

				// Ignore whatever start date came from the browser.
				// Use the server's stored calendar date instead.
				courseForm.setStartDate(calendarStartDate);

				BigDecimal duration = selectedCourse.getDurationDays();

				LocalDate endDate = trainingDayService.calculateEndDate(calendarStartDate, duration);

				courseForm.setEndDate(endDate);
			}

			// Link this application to that Course
			courseForm.setCourse(selectedCourse);
			courseForm.setCourseTitle(selectedCourse.getTitle());
			courseForm.setTrainingProvider(selectedCourse.getProvider().getName());
			courseForm.setFee(selectedCourse.getFee());

			// Ask the service to validate and save the applicant's course request
			applicationService.submit(courseForm, applicant);

			if (fromCalendar) {
				session.removeAttribute("calendarCourseId");
				session.removeAttribute("calendarStartDate");
			}

		} catch (IllegalArgumentException validationError) {
			
			model.addAttribute("error", validationError.getMessage());
			model.addAttribute("courses", courseService.findActive());
			model.addAttribute("fromCalendar", fromCalendar);

			return "application-form";
		}

		redirectAttributes.addFlashAttribute("success", "Course application submitted successfully");

		return "redirect:/applications";
	}

	@GetMapping
	public String showMyApplications(
			HttpSession session,
			@RequestParam(value = "page", defaultValue = "1") int pageNo,
			@RequestParam(value = "size", defaultValue = "10") int pageSize,
			Model model) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		// get the current calendar year
		int currentYear = LocalDate.now().getYear();

		// set the first and last dates of that year
		LocalDate firstDay = LocalDate.of(currentYear, 1, 1);
		LocalDate lastDay = LocalDate.of(currentYear, 12, 31);

		// Retrieve one page of this employee's applications for the current year
		Page<CourseApplication> page =
				applicationService.findMyApplicationsPaginated(
						applicant.getId(),
						firstDay,
						lastDay,
						pageNo,
						pageSize);

		// Actual applications for the current page
		List<CourseApplication> myApplications = page.getContent();

		// Find which applications are currently eligible for reimbursement.
		List<Long> claimableApplicationIds =
				myApplications.stream()
						.filter(claimService::canClaim)
						.map(CourseApplication::getId)
						.toList();

		// Make the application list available to the HTML page
		model.addAttribute("applications", myApplications);

		// Make the claimable application IDs available to the HTML page
		model.addAttribute(
				"claimableApplicationIds",
				claimableApplicationIds);

		// Make the current year available for the page heading
		model.addAttribute("currentYear", currentYear);

		// Pagination information
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("pageSize", pageSize);

		return "application-list";
	}

	// Handle requests to view one course application's details
	@GetMapping("/{id}")
	// @PathVariable("id") Long id -> Read the ID from the URL and store it in id
	public String showApplicationDetails(@PathVariable("id") Long id, HttpSession session, Model model) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		try {

			// Find the requested application and check that it belongs to this employee
			CourseApplication courseRequest = applicationService.findMyApplication(id, applicant);

			// Make the application details available to the HTML page
			model.addAttribute("courseRequest", courseRequest);

		} catch (IllegalArgumentException validationError) {

			// Show the service's error message on the home page
			model.addAttribute("error", validationError.getMessage());

			return "index";
		}

		return "application-detail";
	}

	// Handle requests to open the edit form for one application
	@GetMapping("/{id}/edit")
	public String showEditApplicationForm(@PathVariable("id") Long id, HttpSession session, Model model) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		try {

			CourseApplication courseRequest = applicationService.findMyApplication(id, applicant);

			// allow editing only while the application is pending
			if (courseRequest.getStatus() != ApplicationStatus.APPLIED
					&& courseRequest.getStatus() != ApplicationStatus.UPDATED) {

				throw new IllegalArgumentException("Only pending application can be edited");
			}

			model.addAttribute("courseForm", courseRequest);
			model.addAttribute("courses", courseService.findActive());
			model.addAttribute("fromCalendar", false);

		} catch (IllegalArgumentException validationError) {

			model.addAttribute("error", validationError.getMessage());

			return "index";
		}

		return "application-form";
	}

	@PostMapping("/{id}/update")
	public String updateApplication(@PathVariable("id") Long id,
			@ModelAttribute("courseForm") CourseApplication changes, @RequestParam Long courseId, HttpSession session,
			Model model, RedirectAttributes redirectAttributes) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		try {
			Course selectedCourse = courseService.findById(courseId);

			if (!selectedCourse.isActive()) {
				throw new IllegalArgumentException("Selected course is no longer available");
			}

			// Populate everything controlled by the catalogue
			changes.setCourse(selectedCourse);

			changes.setCourseTitle(selectedCourse.getTitle());

			changes.setTrainingProvider(selectedCourse.getProvider().getName());

			changes.setFee(selectedCourse.getFee());

			applicationService.update(id, changes, applicant);

		} catch (IllegalArgumentException validationError) {

			changes.setId(id);

			model.addAttribute("courseForm", changes);
			model.addAttribute("error", validationError.getMessage());
			model.addAttribute("courses", courseService.findActive());
			model.addAttribute("fromCalendar", false);

			return "application-form";
		}

		redirectAttributes.addFlashAttribute("success", "Course application updated successfully");

		return "redirect:/applications";
	}

	// receive a request to delete the application identified by the URL
	@PostMapping("/{id}/delete")
	public String deleteApplication(@PathVariable("id") Long id, HttpSession session,
			RedirectAttributes redirectAttributes) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		// run the deletion operation, which may report an error
		try {

			// ask the service to delete this application for this employee
			applicationService.delete(id, applicant);

			// handle a rejection, such as deleting another employee's request/approved
			// application
		} catch (IllegalArgumentException validationError) {

			redirectAttributes.addFlashAttribute("error", validationError.getMessage());

			return "redirect:/applications";
		}

		redirectAttributes.addFlashAttribute("success", "Course application deleted successfully");

		return "redirect:/applications";
	}

	@PostMapping("/{id}/cancel")
	public String cancelApplication(@PathVariable("id") Long id, HttpSession session,
			RedirectAttributes redirectAttributes) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		try {

			applicationService.cancel(id, applicant);

		} catch (IllegalArgumentException validationError) {

			redirectAttributes.addFlashAttribute("error", validationError.getMessage());
			return "redirect:/applications";
		}

		redirectAttributes.addFlashAttribute("success", "Course application cancelled successfully");

		return "redirect:/applications";
	}

	@PostMapping("/{id}/complete")
	public String completeApplication(@PathVariable("id") Long id, @RequestParam String experienceComment,
			HttpSession session, RedirectAttributes redirectAttributes) {

		// check if employee have login
		Employee applicant = employeeAuthentication.getEmployee(session);

		if (applicant == null) {
			return "redirect:/login";
		}

		try {

			applicationService.complete(id, applicant, experienceComment);

		} catch (IllegalArgumentException validationError) {
			redirectAttributes.addFlashAttribute("error", validationError.getMessage());

			return "redirect:/applications";
		}

		redirectAttributes.addFlashAttribute("success", "Course application completed successfully");

		return "redirect:/applications";
	}

}
