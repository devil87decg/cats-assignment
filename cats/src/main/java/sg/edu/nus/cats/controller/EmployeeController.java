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
import sg.edu.nus.cats.service.EmployeeService;

@Controller
public class EmployeeController {
	
	// Keep a reference to the service used to create employee profiles.
	private final EmployeeService employeeService;
	
	// Keep a reference to the repository used to check the logged-in user's role.
	private final UserRepository users;
	
	public EmployeeController(EmployeeService employeeService, UserRepository users) {
		
		this.employeeService = employeeService;
		this.users = users;
		
	}
	
	@GetMapping("/admin/employees/new")
	
	public String showEmployeeForm(HttpSession session, Model model) {
		
		// reads the acount ID stored during login, if the session has no userId -> sends the broswer to /login
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
		
		return "employee-form";
	}
	
	// recieve the login account ID submitted by the employee profile form
	@PostMapping("/admin/employees")
	
	public String saveEmployee(@RequestParam Long userId, @RequestParam String name,
			@RequestParam(required = false) String designation,
			@RequestParam(required = false) String department,
			@RequestParam StaffCategory staffCategory,
			@RequestParam (required = false) Long supervisorId,
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
		// ask the service to check the submitted details and save the employee profile
		employeeService.createProfile(userId, name, designation, department, staffCategory, supervisorId);
		
		// display a success message to the next page if saving succeeds
		redirectAttributes.addFlashAttribute(
				"success", "Employee profile created successfully");
		
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
			
			return "employee-edit";
			
		} catch (IllegalArgumentException e) {
			return "redirect:/admin/employees";
		}
	}
	
	@PostMapping("/admin/employees/{id}")
	public String updateEmployee(
			@PathVariable Long id,
			@RequestParam String name,
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
}
