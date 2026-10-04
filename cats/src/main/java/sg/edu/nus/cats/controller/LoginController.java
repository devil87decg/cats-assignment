package sg.edu.nus.cats.controller;

import java.time.Year;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
// give LoginController access to the accounts in MySQL
import sg.edu.nus.cats.repository.UserRepository;

@Controller
public class LoginController {

	// users is the repository the controller will use to find an account by username
	private final UserRepository users;
	// create one BCrypt password-checking object for this controller
	private final BCryptPasswordEncoder passwordEncoder =  new BCryptPasswordEncoder();
	
	public LoginController(UserRepository users) {	
		this.users = users;
	}
	
	@ModelAttribute
	private void addUsersToModel(Model model) {	
		model.addAttribute(
				"users",
				users.findAll());
	}
	
	@GetMapping("/login")
	
	public String showLoginForm(Model model) {
		addUsersToModel(model);

		return "login";
	}
	
	@PostMapping("/login")
	
	public String login(@RequestParam String username, 
			@RequestParam String password,
			HttpSession session,
			Model model) {
		
		// search the users table for the entered username, if no account is found, set user to null
		User user = users.findByUsername(username).orElse(null);
		
		int currentYear = Year.now().getValue(); // Returns 2026 dynamically

		// if no account found -> show login page again
		if (user == null) {
			
			model.addAttribute("error", "Username or password is incorrect");
			
			return "login";
		}
		
		// if this account has no stored password hash/entered password does not match hash -> login page
		if (user.getPasswordHash() == null || !passwordEncoder.matches(
				password, user.getPasswordHash())) {
			
			model.addAttribute("error", "Username or password is incorrect");
			
			return "login";
		}	
		
		if (!user.isActive()) {
			model.addAttribute(
					"error",
					"This account is inactive. Please contact an administrator.");
			return "login";
		}
		
		session.setAttribute("userId", user.getId());
		session.setAttribute("loggedInName", user.getUsername());
		session.setAttribute("year", currentYear);
		
		// if the user is an admin -> browser to open /admin
		if (user.getRole() == Role.ADMIN) {
			
			return "redirect:/admin";
		}
		
		return "redirect:/";
	}
	
	// runs when someone clicks the logout link
	@GetMapping("/logout")
	
	public String logout(HttpSession session) {
		
		// ends the visitor's session, removing the stored userId
		session.invalidate();
		
		return"redirect:/login";
	}
	
	// // Display the shared login form with employee-specific wording
	@GetMapping("/employee/login")
	
	public String showEmployeeLoginForm(Model model) {
		
		model.addAttribute("loginTitle", "Employee Login");
		model.addAttribute("loginDescription",
				"Sign in to manage your course application");
		addUsersToModel(model);
		
		return "login";
		
	}
	
	// Display the shared login form with administrator-specific wording
	@GetMapping("/admin/login")
	
	public String showAdminLoginForm(Model model) {
		
		model.addAttribute("loginTitle", "Adminstrator Login");
		model.addAttribute("loginDescription",
				"Sign in to manage accounts, employee profiles and allowances.");
		
		addUsersToModel(model);
		
		return "login";
	}
	
	
}
