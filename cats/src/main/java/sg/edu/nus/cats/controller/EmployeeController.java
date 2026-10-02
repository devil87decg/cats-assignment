package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.StaffCategory;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.EmployeeService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
}
