package sg.edu.nus.cats.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.AllowanceService;

// tells Spring that this class will handle web requests
@Controller
public class AllowanceController {
	
	private final AllowanceService allowanceService;
	private final UserRepository users;

	public AllowanceController(AllowanceService allowanceService, UserRepository users) {
		
		this.allowanceService = allowanceService;
		this.users = users;
	}
	
	@GetMapping("/admin")
	
	public String showAdminPage(HttpSession session) {
		
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
		
		return "admin";
		
	}
	
	// open the allowance form when this URL is visited 
	@GetMapping("/admin/allowances/new")
	
	public String showAllowanceForm(HttpSession session) {
		
		// get the logged-in user's ID from the session
		Long userId = (Long) session.getAttribute("userId");
		// if nobody is logged in, send them to the login page
		if (userId == null) {
			
			return "redirect:/login";
		}
		
		User user = users.findById(userId).orElse(null);
		
		if (user == null) {
			
			return "redirect:/login";
		}
		
		// if the account is inactive or its role is not ADMIN, does not reach allowance form
		if (!user.isActive() || user.getRole() != Role.ADMIN) {
			
			return "redirect:/";
		}
		
		// show the allowance form to a logged in user
		return "allowance-form";
	}
	
	@PostMapping("/admin/allowances")
	
	public String saveAllowance(@RequestParam Long employeeId, 
			@RequestParam int year,
			@RequestParam BigDecimal dayLimit,
			@RequestParam(required = false) BigDecimal feeBudget,
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
			
			return "redirect:/login";
		}
		
		// pass four submitted values to AllowanceService -> updates info in MySQL
		try {
			
		allowanceService.setAllowance(employeeId, year, dayLimit, feeBudget);
		
		redirectAttributes.addFlashAttribute("success", "Allowance saved successfully");
		
		} catch (IllegalArgumentException error) {
			
			redirectAttributes.addFlashAttribute("error", error.getMessage());
		}
		
		return "redirect:/admin/allowances/new";
	}
	
}
