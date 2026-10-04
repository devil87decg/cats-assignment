package sg.edu.nus.cats.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.service.AllowanceService;
import sg.edu.nus.cats.utils.AdminAuthHelper;

// tells Spring that this class will handle web requests
@Controller
@RequestMapping("/admin")
public class AllowanceController {

	private final AllowanceService allowanceService;
	private final AdminAuthHelper adminAuthentication;
	private final EmployeeRepository employeeRepository;

	public AllowanceController(AllowanceService allowanceService, AdminAuthHelper adminAuthentication,
			EmployeeRepository employeeRepository) {

		this.allowanceService = allowanceService;
		this.adminAuthentication = adminAuthentication;
		this.employeeRepository = employeeRepository;
	}

	@GetMapping
	public String showAdminPage(HttpSession session) {

		User admin = adminAuthentication.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		return "admin";

	}

	// open the allowance form when this URL is visited
	@GetMapping("/allowances/new")
	public String showAllowanceForm(@RequestParam(required = false) Long employeeId, HttpSession session, Model model) {

		User admin = adminAuthentication.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		model.addAttribute("employees", employeeRepository.findAll());
		model.addAttribute("currentYear", allowanceService.getCurrentYear());
		
		// When admin select the employee via dropdown
		if(employeeId != null) {
			
			Employee selectedEmployee = employeeRepository.findById(employeeId).orElse(null);
			
			if(selectedEmployee != null) {
				model.addAttribute("selectedEmployee", selectedEmployee);
				// show the current year allowance for the selected dropdown employee
				model.addAttribute("allowance", allowanceService.getCurrentYearAllowance(employeeId));
			}
		}

		// show the allowance form to a logged in user
		return "allowance-form";
	}

	@PostMapping("/allowances")
	public String saveAllowance(@RequestParam Long employeeId,
			@RequestParam BigDecimal dayLimit, @RequestParam BigDecimal feeBudget,
			HttpSession session, RedirectAttributes redirectAttributes) {

		User admin = adminAuthentication.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		// pass four submitted values to AllowanceService -> updates info in MySQL
		try {
			int currentYear = allowanceService.getCurrentYear();

			allowanceService.setAllowance(employeeId, currentYear, dayLimit, feeBudget);

			redirectAttributes.addFlashAttribute("success", "Allowance saved successfully");

		} catch (IllegalArgumentException error) {

			redirectAttributes.addFlashAttribute("error", error.getMessage());
		}

		return "redirect:/admin/allowances/new?employeeId=" + employeeId;
	}

}
