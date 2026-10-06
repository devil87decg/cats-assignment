package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.AccountService;

@Controller
public class AccountController {

    private final UserRepository users;
    private final AccountService accountService;

    public AccountController(UserRepository users,
                             AccountService accountService) {
        this.users = users;
        this.accountService = accountService;
    }
	
	@GetMapping("/account/password")
	public String showChangePasswordForm(HttpSession session, Model model) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null || !user.isActive()) {
			return "redirect:/login";
		}
		
		 model.addAttribute("role", user.getRole());
		
		return "change-password";
	}
	
	@PostMapping("/account/password")
	public String changePassword(
			@RequestParam String currentPassword,
			@RequestParam String newPassword,
			@RequestParam String confirmPassword,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null || !user.isActive()) {
			return "redirect:/login";
		}
		
		try {
			
			accountService.changePassword(
					userId,
					currentPassword,
					newPassword,
					confirmPassword);
			
			redirectAttributes.addFlashAttribute(
					"success",
					"Password changed successfully");
			
		} catch (IllegalArgumentException e) {
			
			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}
		
		return "redirect:/account/password";
	}
}
