package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.AccountService;

@Controller
public class AccountController {

	// Keep a reference to the service used to create login accounts.
	private final AccountService accounts;
	
	private final UserRepository users;
	
	public AccountController(AccountService accounts, UserRepository users) {
		
		this.accounts = accounts;
		this.users = users;
		
	}
	
	// open the login account creation form
	@GetMapping("/admin/accounts/new")
	
	// give the method access to the visitor's login session
	public String showAccountForm(HttpSession session, Model model) {
		
		// read the logged-in account's ID from the session
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		// send visitors without a logged-in account to the login page
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		// ask repository to find that ID in the users table
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		// return to login if the account cannot be found in database
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.ADMIN) {
			
			return "redirect:/";
		}
		
		// make all account roles from Enum available to the form's dropdown
		model.addAttribute("roles", Role.values());
		
		// an Active admin passes both checks and reaches
		return "account-form";
	}
	
	// Receive the username submitted by the account creation form.
	@PostMapping("/admin/accounts")
	
	public String saveAccount(@RequestParam String username,
			@RequestParam String password,
			@RequestParam Role role,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		// Read the logged-in account's ID from the session.
		Long loggedInUserId = (Long) session.getAttribute("userId");
		
		if (loggedInUserId == null) {
			
			return "redirect:/login";
		}
		
		// Find the account belonging to the logged-in visitor.
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		
		// means no matching account was found in the database
		if (loggedInUser == null) {
			
			return "redirect:/login";
		}
		
		// Allow only active admins to create login accounts.
		if (!loggedInUser.isActive() || loggedInUser.getRole() != Role.ADMIN) {
			
			return "redirect:/";
		}
		
		try {
		// create the account in MySQL database and keep the saved results, including its generated ID
		User createdUser = accounts.createAccount(username, password, role);
		
		// Show the new account ID so the admin can use it to create an employee profile.
		redirectAttributes.addFlashAttribute(
				"success", "Account created successfully. Login account ID: " + createdUser.getId());
		
		} catch (IllegalArgumentException validationError) {
			
			//  show the explanation that caused the error supplied by service 
			redirectAttributes.addFlashAttribute(
					"error", validationError.getMessage());
			
		}
		
		return "redirect:/admin/accounts/new";
	}
	
}
