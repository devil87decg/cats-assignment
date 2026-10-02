package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;

@Controller
public class HomeController {

    // Keep the repository used to find the logged-in account
    private final UserRepository users;

    // Receive the repository from Spring
    public HomeController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {

        // Read the logged-in account's ID from the session
        Long loggedInUserId = (Long) session.getAttribute("userId");

        // Send visitors without a login session to the login page
        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        // Find the account matching the session's user ID
        User loggedInUser = users.findById(loggedInUserId).orElse(null);

        // Require an account that still exists
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Prevent inactive accounts from accessing the home page
        if (!loggedInUser.isActive()) {
            return "redirect:/login";
        }

        // Tell the home page whether this account is a manager
        model.addAttribute("isManager",
                loggedInUser.getRole() == Role.MANAGER);

        // Display index.html
        return "index";
    }
}
