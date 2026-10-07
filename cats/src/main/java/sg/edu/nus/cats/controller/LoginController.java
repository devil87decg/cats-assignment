package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

	
	@GetMapping("/login")
	
	public String showLoginForm(Model model) {


		return "login";
	}
	
	// // Display the shared login form with employee-specific wording
	@GetMapping("/employee/login")
	
	public String showEmployeeLoginForm(Model model) {
		
		model.addAttribute("loginTitle", "Employee Login");
		model.addAttribute("loginDescription",
				"Sign in to manage your course application");

		return "login";
		
	}
	
	// Display the shared login form with administrator-specific wording
	@GetMapping("/admin/login")
	
	public String showAdminLoginForm() {
		
	
		return "login";
	}
	
	
}
