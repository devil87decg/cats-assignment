package sg.edu.nus.cats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.CourseCategoryService;

@AllArgsConstructor
@Controller
public class CourseCategoryController {
	private final CourseCategoryService categoryService;
	private final UserRepository users;
	
	@GetMapping("/admin/course-categories")
	public String showCategories(
			HttpSession session,
			Model model) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"categories",
				categoryService.findAll());

		return "course-category-list";
	}


	@GetMapping("/admin/course-categories/new")
	public String showCreateForm(
			HttpSession session) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		return "course-category-form";
	}


	@PostMapping("/admin/course-categories")
	public String createCategory(
			@RequestParam String code,
			@RequestParam String name,
			@RequestParam(required = false) String description,
			@RequestParam(defaultValue = "false")
				boolean internalTraining,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			categoryService.create(
					code,
					name,
					description,
					internalTraining);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course category created successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());

			return "redirect:/admin/course-categories/new";
		}

		return "redirect:/admin/course-categories";
	}


	private User getAdmin(HttpSession session) {

		Long userId =
				(Long) session.getAttribute("userId");

		if (userId == null) {
			return null;
		}

		User user =
				users.findById(userId).orElse(null);

		if (user == null
				|| !user.isActive()
				|| user.getRole() != Role.ADMIN) {

			return null;
		}

		return user;
	}
	
	@GetMapping("/admin/course-categories/{id}/edit")
	public String showEditForm(
			@PathVariable Long id,
			HttpSession session,
			Model model) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			model.addAttribute(
					"category",
					categoryService.findById(id));

			return "course-category-edit";

		} catch (IllegalArgumentException e) {

			return "redirect:/admin/course-categories";
		}
	}
	
	@PostMapping("/admin/course-categories/{id}")
	public String updateCategory(
			@PathVariable Long id,
			@RequestParam String code,
			@RequestParam String name,
			@RequestParam(required = false) String description,
			@RequestParam(defaultValue = "false")
				boolean internalTraining,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			categoryService.update(
					id,
					code,
					name,
					description,
					internalTraining);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course category updated successfully");

			return "redirect:/admin/course-categories";

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());

			return "redirect:/admin/course-categories/"
					+ id + "/edit";
		}
	}
	
	@PostMapping("/admin/course-categories/{id}/deactivate")
	public String deactivateCategory(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			categoryService.deactivate(id);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course category deactivated successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}

		return "redirect:/admin/course-categories";
	}
	
	@PostMapping("/admin/course-categories/{id}/reactivate")
	public String reactivateCategory(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			categoryService.reactivate(id);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course category reactivated successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}

		return "redirect:/admin/course-categories";
	}
}
