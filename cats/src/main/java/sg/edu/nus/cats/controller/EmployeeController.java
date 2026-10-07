package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.StaffCategory;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.AccountService;
import sg.edu.nus.cats.service.EmployeeService;

@Controller
public class EmployeeController {
	
	// Keep a reference to the service used to create employee profiles.
	private final EmployeeService employeeService;
	
	// Keep a reference to the repository used to check the logged-in user's role.
	private final UserRepository users;
	
	private final AccountService accountService;
	
	public EmployeeController(EmployeeService employeeService, UserRepository users, AccountService accountService) {
		
		this.employeeService = employeeService;
		this.users = users;
		this.accountService = accountService;
		
	}
	
	@GetMapping("/admin/employees/new")
	
	public String showEmployeeForm(HttpSession session, Model model) {
		
		// reads the account ID stored during login, if the session has no userId ->
		// sends the browser to /login
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			
			return "redirect:/login";
		}
		
		// finds the User account identified by the session
		User user = users.findById(userId).orElse(null);
		
		// if that account was not found in MySQ -> send the browser to login
		if (user == null) {
			
			return "redirect:/login";
		}
		
		// if account is inactive or its role is not ADMIN -> send browser to home page
		if (!user.isActive() || user.getRole() != Role.ADMIN) {
			
			return "redirect:/";
		}
		
		// make the staff category choices available to HTML form
		model.addAttribute("staffCategories", StaffCategory.values());
		
		//Allow admin to choose user roles when creating employee
		model.addAttribute(
				"roles",
				Role.values());

		//Allow admin to select supervisor
		model.addAttribute(
				"supervisors",
				employeeService.findManagers());
		
		return "employee-form";
	}
	
	// receive the login account ID submitted by the employee profile form
	@PostMapping("/admin/employees")
	
	public String saveEmployee(
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam Role role,
			@RequestParam String name,
			@RequestParam String email,
			@RequestParam(required = false) String designation,
			@RequestParam(required = false) String department,
			@RequestParam StaffCategory staffCategory,
			@RequestParam(required = false) Long supervisorId,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		// read the logged-in account's ID from the session
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		// send visitors without a logged-in account to the login page
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		// repository searches the users table belonging to the logged-in user's ID
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		// allow only active admin accounts to create employee profiles
		if(!loggedInUser.isActive() || loggedInUser.getRole() != Role.ADMIN) {
			
			return "redirect/";
		}
		
		try {
		//Create a login details, then employee in one transaction.
			employeeService.createEmployeeWithAccount(
					username,
					password,
					role,
					name,
					email,
					designation,
					department,
					staffCategory,
					supervisorId);
		
		// display a success message to the next page if saving succeeds
		redirectAttributes.addFlashAttribute(
				"success", "Employee account and profile created successfully");
		
		} catch (IllegalArgumentException validationError) {
			
			// display the service's validation error message to the next page 
			redirectAttributes.addFlashAttribute(
					"error", validationError.getMessage());
		}
		
		return "redirect:/admin/employees/new";
	}
	
	@GetMapping("/admin/employees")
	public String showEmployees(HttpSession session, Model model) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		if (!user.isActive() || user.getRole() != Role.ADMIN) {
			return "redirect:/";
		}
		
		model.addAttribute("employees", employeeService.findAllEmployees());
		
		return "employee-list";
	}
	
	//Edit employee
	@GetMapping("/admin/employees/{id}/edit")
	public String showEditEmployeeForm(
			@PathVariable Long id,
			HttpSession session,
			Model model) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		if (!user.isActive() || user.getRole() != Role.ADMIN) {
			return "redirect:/";
		}
		
		try {
			
			Employee employee = employeeService.findEmployeeById(id);
			
			model.addAttribute("employee", employee);
			model.addAttribute("staffCategories", StaffCategory.values());
			model.addAttribute("supervisors", employeeService.findManagers());
			model.addAttribute("roles", Role.values());
			
			return "employee-edit";
			
		} catch (IllegalArgumentException e) {
			return "redirect:/admin/employees";
		}
	}
	
	@PostMapping("/admin/employees/{id}")
	public String updateEmployee(
			@PathVariable Long id,
			@RequestParam String name,
			@RequestParam String email,
			@RequestParam Role role,
			@RequestParam(required = false) String designation,
			@RequestParam(required = false) String department,
			@RequestParam StaffCategory staffCategory,
			@RequestParam(required = false) Long supervisorId,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		if (!user.isActive() || user.getRole() != Role.ADMIN) {
			return "redirect:/";
		}
		
		try {
			
			employeeService.updateEmployee(
					id,
					name,
					email,
					role,
					designation,
					department,
					staffCategory,
					supervisorId);
			
			redirectAttributes.addFlashAttribute(
					"success",
					"Employee profile updated successfully");
			
			return "redirect:/admin/employees";
			
		} catch (IllegalArgumentException e) {
			
			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
			
			return "redirect:/admin/employees/" + id + "/edit";
		}
	}
	
	// Delete employee
	@PostMapping("/admin/employees/{id}/delete")
	public String deleteEmployee(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		if (!user.isActive() || user.getRole() != Role.ADMIN) {
			return "redirect:/";
		}
		
		try {
			
			boolean deleted = employeeService.deleteEmployee(id);
			
			if (deleted) {

				redirectAttributes.addFlashAttribute(
						"success",
						"Employee and login account deleted successfully");

			} else {

				redirectAttributes.addFlashAttribute(
						"success",
						"Employee has application history and was deactivated instead of deleted");
			}
			
		} catch (IllegalArgumentException e) {
			
			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}
		
		return "redirect:/admin/employees";
	}
	
	@PostMapping("/admin/employees/{id}/password")
	public String resetEmployeePassword(
			@PathVariable Long id,
			@RequestParam String newPassword,
			@RequestParam String confirmPassword,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long loggedInUserId =
				(Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			return "redirect:/login";
		}
		
		User loggedInUser =
				users.findById(loggedInUserId).orElse(null);
		
		if (loggedInUser == null) {
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive()
				|| loggedInUser.getRole() != Role.ADMIN) {
			
			return "redirect:/";
		}
		
		try {
			
			Employee employee =
					employeeService.findEmployeeById(id);
			
			accountService.resetPassword(
					employee.getUser().getId(),
					newPassword,
					confirmPassword);
			
			redirectAttributes.addFlashAttribute(
					"success",
					"Password reset successfully");
			
		} catch (IllegalArgumentException e) {
			
			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}
		
		return "redirect:/admin/employees/" + id + "/edit";
	}
}
