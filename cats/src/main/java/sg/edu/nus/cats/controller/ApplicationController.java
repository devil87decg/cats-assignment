package sg.edu.nus.cats.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.Course;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.CourseCategory;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.ApplicationService;
import sg.edu.nus.cats.service.CourseService;

@AllArgsConstructor
@Controller
public class ApplicationController {

	// Keep a reference to the service that validates and manages course applications.
	private final ApplicationService applicationService;
	
	// Find the employee profile linked to the logged-in account.
	private final EmployeeRepository employees;
	
	private final UserRepository users;
	private final CourseService courseService;
	
	// open the form for submitting a new course application
	@GetMapping("/applications/new")
	
	public String showApplicationForm(HttpSession session, Model model) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		// Find the login account matching the ID stored in the session
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		// return to login if the account no longer exists
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		// prevent inactive accounts from opening the course application form
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		// find the employee profile belonging to the logged in account
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		// show an explanation if the account has no employee profile 
		if (applicant == null) {
			
			model.addAttribute(
					"error", "Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		// Create an empty application object for the form fields
		CourseApplication courseForm = new CourseApplication();
		
		// make the application object available to the HTML form
		model.addAttribute("courseForm", courseForm);
		
		// make all course categories available to the form's dropdown
		model.addAttribute("categories", CourseCategory.values());
		
		model.addAttribute(
				"courses",
				courseService.findActive());
		
		return "application-form";
	}
	
	// Receive the course details submitted by the application form
	@PostMapping("/applications")
	
	public String submitApplication(
			@ModelAttribute("courseForm") CourseApplication courseForm,
			@RequestParam Long courseId,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		// Read the account ID of the person submitting the application
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		// Require login before accepting a course application
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		// Find the login account belonging to the person submitting the application
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		// Return to login if the account cannot be found
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if(!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		// Find the applicant's employee profile using their login account ID.
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		// Show an explanation if the logged-in account has no employee profile
		if(applicant == null) {
			model.addAttribute(
					"error", "Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		try {
			Course selectedCourse =
					courseService.findById(courseId);

			if (!selectedCourse.isActive()) {
				throw new IllegalArgumentException(
						"Selected course is no longer available");
			}
			
			//Link this application to that Course
			courseForm.setCourse(selectedCourse);
			
			courseForm.setCourseTitle(
					selectedCourse.getTitle());

			courseForm.setTrainingProvider(
					selectedCourse.getProvider().getName());

			courseForm.setFee(
					selectedCourse.getFee());
			
			// Ask the service to validate and save the applicant's course request
			applicationService.submit(courseForm, applicant);
		
		} catch (IllegalArgumentException validationError) {
			
			model.addAttribute("error", validationError.getMessage());
			model.addAttribute("categories", CourseCategory.values());
			model.addAttribute(
			        "courses",
			        courseService.findActive());
			
			return "application-form";
		}
		
		redirectAttributes.addFlashAttribute("success", "Course application submitted successfully");
		
		return "redirect:/applications/new";
	}
	
	@GetMapping("/applications")
	
	public String showMyApplications(HttpSession session, Model model) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (applicant == null) {
			
			model.addAttribute("error", "Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		// get the current calendar year
		int currentYear = LocalDate.now().getYear();
		
		// set the first and last dates of that year
		LocalDate firstDay = LocalDate.of(currentYear, 1, 1);
		LocalDate lastDay = LocalDate.of(currentYear, 12, 31);
		
		// Retrieve this employee's applications for the current year
		List<CourseApplication> myApplications = applicationService.findMyApplications(
				applicant.getId(), firstDay, lastDay);
		
		// Make the application list available to the HTML page
		model.addAttribute("applications", myApplications);
		
		// Make the current year available for the page heading
		model.addAttribute("currentYear", currentYear);
		
		return "application-list";
		
	}
	
	// Handle requests to view one course application's details
	@GetMapping("/applications/{id}")
	// @PathVariable("id") Long id -> Read the ID from the URL and store it in id
	public String showApplicationDetails(@PathVariable("id") Long id,
			HttpSession session, Model model) {
		
		// Read the logged-in account ID from the session
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		// Require login before displaying course application details
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		// Find the account identified by the session
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		// Return to login if the account cannot be found
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		// Return to login if the account is inactive
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		// Find the employee profile linked to the logged-in account
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		// Show an explanation if the account has no employee profile
		if (applicant == null) {
			
			model.addAttribute("error", "Ask an adminstrator to create your employee profile first");
			
			return "index";
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
	@GetMapping("/applications/{id}/edit")
	
	public String showEditApplicationForm(@PathVariable("id") Long id,
			HttpSession session,
			Model model) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
				
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (applicant == null) {
			
			model.addAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		try {
			
			CourseApplication courseRequest = applicationService.findMyApplication(id, applicant);
			
			// allow editing only while the application is pending 
			if (courseRequest.getStatus() != ApplicationStatus.APPLIED
					&& courseRequest.getStatus() != ApplicationStatus.UPDATED) {
				
				throw new IllegalArgumentException("Only pending application can be edited");
			}
			
			model.addAttribute("courseForm", courseRequest);
			
			model.addAttribute("categories", CourseCategory.values());
			
		} catch (IllegalArgumentException validationError) {
			
			model.addAttribute("error", validationError.getMessage());
			
			return "index";
		}
		
		
		return "application-form";
	}
	
	@PostMapping("/applications/{id}/update")
	
	public String updateApplication(
			@PathVariable("id") Long id,
			@ModelAttribute("courseForm") CourseApplication changes,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
			
		}
		
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (applicant == null) {
			
			model.addAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		try {
			
			applicationService.update(id, changes, applicant);
		
		} catch (IllegalArgumentException validationError) {
			
			changes.setId(id);
			
			model.addAttribute("courseForm", changes);
			model.addAttribute("error", validationError.getMessage());
			model.addAttribute("categories", CourseCategory.values());
			
			return "application-form";
		}
		
		redirectAttributes.addFlashAttribute("success",
				"Course application updated successfully");
		
		return "redirect:/applications";
	}
	
	// receive a request to delete the application identified by the URL
	@PostMapping("/applications/{id}/delete")
	
	public String deleteApplication(@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
			
		}
		
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		Employee applicant  = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (applicant == null) {
			
			redirectAttributes.addFlashAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "redirect:/";
		}
		
		// run the deletion operation, which may report an error
		try {
			
			// ask the service to delete this application for this employee
			applicationService.delete(id, applicant);
			
		// handle a rejection, such as deleting another employee's request/approved application	
		} catch (IllegalArgumentException validationError) {
			
			redirectAttributes.addFlashAttribute("error",
					validationError.getMessage());
			
			return "redirect:/applications";
		}
		
		redirectAttributes.addFlashAttribute("success",
				"Course application deleted successfully");
		
		return "redirect:/applications";
	}
	
	@PostMapping("/applications/{id}/cancel")
	
	public String cancelApplication(@PathVariable("id") Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (applicant == null) {
			
			redirectAttributes.addFlashAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "redirect:/";
		}
		
		try {
			
			applicationService.cancel(id, applicant);
		
		} catch (IllegalArgumentException validationError) {
				
			redirectAttributes.addFlashAttribute("error",
					validationError.getMessage());
			
		}
		
		redirectAttributes.addFlashAttribute("success",
				"Course application cancelled successfully");
		
		return "redirect:/applications";
	}
	
	@PostMapping("/applications/{id}/complete")
	
	public String completeApplication(@PathVariable("id") Long id,
			@RequestParam String experienceComment,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive()) {
			
			return "redirect:/login";
		}
		
		Employee applicant = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (applicant == null) {
			
			redirectAttributes.addFlashAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "redirect:/";
		}
		
		try {
			
			applicationService.complete(id, applicant, experienceComment);
		
		} catch(IllegalArgumentException validationError) {
			
			redirectAttributes.addFlashAttribute("error",
					validationError.getMessage());
		}
		
		return "redirect:/applications";
	}

}
