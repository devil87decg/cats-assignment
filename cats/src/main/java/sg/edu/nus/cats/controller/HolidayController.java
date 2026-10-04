package sg.edu.nus.cats.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.HolidayService;
import sg.edu.nus.cats.utils.AdminAuthHelper;

@Controller
@RequestMapping("/admin/holidays")
public class HolidayController {

	private final HolidayService holidayService;
//	private final UserRepository userRepository;
	private final AdminAuthHelper adminAuthentication;

	public HolidayController(HolidayService holidayService, AdminAuthHelper adminAuthentication) {
		this.holidayService = holidayService;
		this.adminAuthentication = adminAuthentication;
	}

	@GetMapping
	public String showHolidays(HttpSession session, Model model) {
		User admin = adminAuthentication.getAdmin(session);
		if (admin == null) {
			return "redirect:/";
		}

		model.addAttribute("holidays", holidayService.findAllHolidays());

		return "holiday-list";
	}

	@GetMapping("/new")
	public String showCreateHolidayForm(HttpSession session) {
		User admin = adminAuthentication.getAdmin(session);
		if (admin == null) {
			return "redirect:/";
		}

		return "holiday-form";
	}

	@PostMapping
	public String createHoliday(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam String name, HttpSession session, RedirectAttributes redirectAttributes) {
		User admin = adminAuthentication.getAdmin(session);
		if (admin == null) {
			return "redirect:/";
		}
		
		try {
			holidayService.createHoliday(date, name);
			redirectAttributes.addFlashAttribute("success", "Public holiday added successfully");
			return "redirect:/admin/holidays";
		} catch (IllegalArgumentException error) {
			redirectAttributes.addFlashAttribute("error", error.getMessage());
			return "redirect:/admin/holidays/new";
		}
	}
	
	@GetMapping("/{id}/edit")
	public String showEditHolidayForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		User admin = adminAuthentication.getAdmin(session);
		if (admin == null) {
			return "redirect:/";
		}
		
		try {
			model.addAttribute("holiday", holidayService.findHoliday(id));
			return "holiday-edit";
		}catch(IllegalArgumentException error) {
			redirectAttributes.addFlashAttribute("error", error.getMessage());
		}
		
		return "redirect:/admin/holidays";
	}
	
	@PostMapping("/{id}")
	public String updateHoliday(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam String name, HttpSession session, RedirectAttributes redirectAttributes) {
		User admin = adminAuthentication.getAdmin(session);
		if (admin == null) {
			return "redirect:/";
		}
		
		try {
			holidayService.updateHoliday(id, date, name);
			redirectAttributes.addFlashAttribute("success", "Public holiday updated successfully");
			return "redirect:/admin/holidays";
		} catch (IllegalArgumentException error) {
			redirectAttributes.addFlashAttribute("error", error.getMessage());
			return "redirect:/admin/holidays/" + id +"/edit";
		}
	}
	
	@PostMapping("/{id}/delete")
	public String deleteHoliday(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		User admin = adminAuthentication.getAdmin(session);
		if (admin == null) {
			return "redirect:/";
		}
		
		try {
			holidayService.deleteHoliday(id);
			redirectAttributes.addFlashAttribute("success", "Public holiday deleted successfully");
		} catch (IllegalArgumentException error) {
			redirectAttributes.addFlashAttribute("error", error.getMessage());
		}
		return "redirect:/admin/holidays";
		
	}

}
