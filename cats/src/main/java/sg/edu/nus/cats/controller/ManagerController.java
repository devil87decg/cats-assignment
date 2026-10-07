package sg.edu.nus.cats.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.ApplicationService;
import sg.edu.nus.cats.service.EmployeeService;

@Controller
public class ManagerController {

	private final ApplicationService applicationService;
	
	private final EmployeeRepository employees;
	
	private final UserRepository users;
	
	private final EmployeeService employeeService;
	
	public ManagerController (ApplicationService applicationService, EmployeeRepository employees, UserRepository users,
			EmployeeService employeeService) {
		
		this.applicationService = applicationService;
		this.employees = employees;
		this.users = users;
		this.employeeService = employeeService;
	}
	
	@GetMapping("/manager/applications")
	
	public String showPendingApplications(HttpSession session, Model model) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		// Allow only active manager accounts to open this page
		if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.MANAGER) {
			
			return "redirect:/";
		}
		
		Employee manager = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (manager == null) {
			
			model.addAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		// Retrieve pending applications belonging to this manager's subordinates
		List<CourseApplication> pendingApplications = applicationService.findPendingApplications(manager);
		
		// Make the pending applications available to the HTML page
		model.addAttribute("pendingApplications", pendingApplications);
		
		return "manager-application-list";
	}
	
	// Open the manager's review page for one course application.
	@GetMapping("/manager/applications/{id}")
	
	public String showApplicationReview(@PathVariable("id") Long id,
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
		
		// Allow only active manager accounts to review applications
		if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.MANAGER) {
		
			return "redirect:/";
			
		}
		
		// Find the employee profile linked to the manager's login account
		Employee manager = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (manager == null) {
			model.addAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		try {
			
			// Find the application and check that the applicant reports to this manager
			CourseApplication courseRequest = applicationService.findApplicationForReview(id, manager);
			
			// Read the applicant and the course year for the usage calculation
			Employee applicant = courseRequest.getEmployee();
			int courseYear = courseRequest.getStartDate().getYear();
			
			// establish first and last dates variables of the course year
			LocalDate firstDay = LocalDate.of(courseYear, 1, 1);
			LocalDate lastDay = LocalDate.of(courseYear, 12, 31);
			
			// Calculate the applicant's reserved and used training days and fees
			BigDecimal usedDays = applicationService.calculateUsedDays(applicant, courseYear);
			
			BigDecimal usedFees = applicationService.calculateUsedFees(applicant, courseYear);
			
			BigDecimal completedDays = applicationService.calculateCompletedDays(
					courseRequest.getEmployee().getId(), firstDay, lastDay);
			
			BigDecimal reservedDays = usedDays.subtract(completedDays);
			
			BigDecimal completedFees = applicationService.calculateCompletedFees(applicant.getId(), firstDay, lastDay);
			
			BigDecimal reservedFees = usedFees.subtract(completedFees);
			
			// Make the year and annual usage totals available to the HTML page
			model.addAttribute("courseYear", courseYear);
			model.addAttribute("usedDays", usedDays);
			model.addAttribute("usedFees", usedFees);
			model.addAttribute("completedDays", completedDays);
			model.addAttribute("reservedDays", reservedDays);
			model.addAttribute("completedFees", completedFees);
			model.addAttribute("reservedFees", reservedFees);
			
			// Make the application available to the review page
			model.addAttribute("courseRequest", courseRequest);
			
			// find other subordinate's approved courses during this course period
			List<CourseApplication> otherApprovedApplications = 
					applicationService.findOtherApprovedApplications(courseRequest, manager);
			
			// make these courses available to the review page
			model.addAttribute("otherApprovedApplications", otherApprovedApplications);
			
		} catch (IllegalArgumentException validationError) {
			
			model.addAttribute("error", validationError.getMessage());
			
			model.addAttribute("isManager", true);
			
			return "index";
		}
			
		return "manager-application-detail";
	}
	
	// Receive the manager's decision and its reason
	@PostMapping("/manager/applications/{id}/decide")
	
	public String decideApplication(@PathVariable("id") Long id,
			@RequestParam boolean approved,
			@RequestParam String reason,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		// Require login before accepting an approval or rejection
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		// Allow only active manager accounts to approve or reject applications
		if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.MANAGER) {
			
			return "redirect:/";
		}
		
		Employee manager = employees.findByUserId(loggedInUserId).orElse(null);
		
		if (manager == null) {
			
			redirectAttributes.addFlashAttribute("error",
					"Ask an adminstrator to create your employee profile first");
			
			return "redirect:/";
		}
		
		try  {
			
			 // Check and save the manager's decision and reason
			applicationService.decide(id, manager, approved, reason);
			
		} catch (IllegalArgumentException validationError) {
			
			redirectAttributes.addFlashAttribute("error", validationError.getMessage());
			
			return "redirect:/manager/applications";
		}
		
		// Confirm that the manager's decision was saved
		redirectAttributes.addFlashAttribute("success", "Course application decision saved successfully");
		
		return "redirect:/manager/applications";
	}
	
	// Open the page where a manager selects a subordinate's course history
	@GetMapping("/manager/subordinates")
	
	public String showSubordinates(HttpSession session, Model model) {
		
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.MANAGER) {
			
			return "redirect:/";
		}
		
		Employee manager = employees.findByUserId(loggedInUserId).orElse(null); 
		
		if (manager == null) {
			
			model.addAttribute("error", "Ask an adminstrator to create your employee profile first");
			
			return "index";
		}
		
		// call the service, which asks the repository to find this manager's subordinates
		List<Employee> subordinates = employeeService.findSubordinates(manager);
		
		model.addAttribute("subordinates", subordinates);
		
		return "manager-subordinates";
	}
	
	// Open the course history page for the selected subordinate
	@GetMapping("/manager/subordinates/{id}/history")
	
	public String showSubordinateHistory(
			@PathVariable("id") Long employeeId,
			@RequestParam(name = "page", defaultValue = "1") int pageNo,
			@RequestParam(name = "size", defaultValue = "5") int pageSize,
			// defaultValue = "false" -> manager has not requested all years, 
			// so default show all employee's application for current year
			@RequestParam(name = "allYears", defaultValue = "false") boolean allYears,
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
			
			// Allow only active manager accounts to view subordinate history
			if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.MANAGER) {
				
				
				return "redirect:/";

			}
			
			// Find the employee profile linked to the manager's login account
			Employee manager = employees.findByUserId(loggedInUserId).orElse(null);
			
			// Stop if the manager has no employee profile
			if (manager == null) {
				
				model.addAttribute("error", "Ask an adminstrator to create your employee profile first");
				
				return "index";
			}
			
			try {
				
				// Find the selected employee and check that they report to this manager
				Employee subordinate = employeeService.findSubordinate(employeeId, manager);
				
				// get current year and its first and last dates
				int currentYear = LocalDate.now().getYear();
				LocalDate firstDay = LocalDate.of(currentYear, 1, 1);
				LocalDate lastDay = LocalDate.of(currentYear, 12, 31);
				
				// Declare the list that will hold the selected course history
				Page<CourseApplication> page;
				
				// if allYears is true -> retrieve all saved history
				if (allYears) {
				    page = applicationService.findEmployeeHistoryPaginated(
				            subordinate,
				            pageNo,
				            pageSize);
				}
				
				// else -> retrieve applications whose start dates fall within the current year	
				else {
				    page = applicationService.findMyApplicationsPaginated(
				            subordinate.getId(),
				            firstDay,
				            lastDay,
				            pageNo,
				            pageSize);
				}
				
				List<CourseApplication> courseHistory = page.getContent();
				
				// supply the employee profile and course history to HTML
				model.addAttribute("subordinate", subordinate);
				model.addAttribute("courseHistory", courseHistory);
				
				// supply the current year for page heading
				model.addAttribute("currentYear", currentYear);
				
				// tell the page whether it is displaying history from all years
				model.addAttribute("allYears", allYears);
				
				// Pagination
				model.addAttribute("currentPage", pageNo);
				model.addAttribute("totalPages", page.getTotalPages());
				model.addAttribute("totalItems", page.getTotalElements());
				model.addAttribute("pageSize", pageSize);
				
			} catch (IllegalArgumentException validationError) {
				
				model.addAttribute("error", validationError.getMessage());
				
				// Keep manager links visible on the home page when an error occurs.
				model.addAttribute("isManager", true);
				
				return "index";
				
			}
	
			return "manager-subordinate-history";
	}
	
}
