package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.utils.AdminAuthHelper;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminAuthHelper adminAuthentication;

    @GetMapping
    public String showAdminPage(HttpSession session, Model model) {

        User admin = adminAuthentication.getAdmin(session);

        if (admin == null) {
            return "redirect:/";
        }

        // Keep calendar navigation inside the admin page
        model.addAttribute("calendarBaseUrl", "/admin");

        return "admin";
    }

}
