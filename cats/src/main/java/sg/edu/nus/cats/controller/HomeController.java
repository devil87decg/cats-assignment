package sg.edu.nus.cats.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.ApplicationService;

@Controller
public class HomeController {

	// Keep the repository used to find the logged-in account
	private final UserRepository users;
	private final ApplicationService applicationService;
	private final EmployeeRepository employees;

	// Receive the repository from Spring
	public HomeController(UserRepository users, ApplicationService applicationService, EmployeeRepository employees) {
		this.users = users;
		this.applicationService = applicationService;
		this.employees = employees;
	}

	@GetMapping("/")
	public String home(HttpSession session, Model model) {

		// Read the logged-in account's ID from the session
		Long loggedInUserId = (Long) session.getAttribute("userId");
		Integer loggedInYear = (Integer) session.getAttribute("year");

		// Send visitors without a login session to the login page
		if (loggedInUserId == null) {
			return "redirect:/login";
		}

		if (loggedInYear == null) {
			loggedInYear = getSessionYear(session);
		}

		// Find the account matching the session's user ID
		User loggedInUser = users.findById(loggedInUserId).orElse(null);
		Employee loggedInEmp = employees.findByUserId(loggedInUserId).orElse(null);

		BigDecimal remainingDays = applicationService.balanceDays(loggedInEmp, loggedInYear);
		BigDecimal remainingFees = applicationService.balanceBudget(loggedInEmp, loggedInYear);

		// Require an account that still exists
		if (loggedInUser == null) {
			return "redirect:/login";
		}

		// Prevent inactive accounts from accessing the home page
		if (!loggedInUser.isActive()) {
			return "redirect:/login";
		}

		// Tell the home page whether this account is a manager
		model.addAttribute("isManager", loggedInUser.getRole() == Role.MANAGER);
		model.addAttribute("remainingDays", remainingDays);
		model.addAttribute("remainingFees", remainingFees);
		// Display index.html
		return "index";
	}

	// helper function to get year of log in
	public int getSessionYear(HttpSession session) {
		// Get creation time in milliseconds
		long creationTime = session.getCreationTime();

		// Convert milliseconds to a Year using Instant and ZonedDateTime
		int year = ZonedDateTime.ofInstant(Instant.ofEpochMilli(creationTime), ZoneId.systemDefault()).getYear();

		return year;
	}
}
