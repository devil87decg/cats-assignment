package sg.edu.nus.cats.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.Course;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.service.CourseCategoryService;
import sg.edu.nus.cats.service.CourseService;
import sg.edu.nus.cats.service.TrainingProviderService;
import sg.edu.nus.cats.utils.AdminAuthHelper;

@AllArgsConstructor
@Controller
public class CourseController {
	private final CourseService courseService;
	private final CourseCategoryService categoryService;
	private final TrainingProviderService providerService;
	private final AdminAuthHelper adminAuth;
	
	@GetMapping("/admin/courses")
	public String showCourses(
			HttpSession session,
			Model model) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"courses",
				courseService.findAll());

		return "course-list";
	}


	@GetMapping("/admin/courses/new")
	public String showCreateForm(
			HttpSession session,
			Model model) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"categories",
				categoryService.findActive());

		model.addAttribute(
				"providers",
				providerService.findActive());

		return "course-form";
	}


	@PostMapping("/admin/courses")
	public String createCourse(
			@RequestParam String code,
			@RequestParam String title,
			@RequestParam(required = false) String description,
			@RequestParam Long categoryId,
			@RequestParam Long providerId,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal fee,
			@RequestParam BigDecimal durationDays,
			@RequestParam(defaultValue = "false")
				boolean internalHalfDay,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			courseService.create(
					code,
					title,
					description,
					categoryId,
					providerId,
					location,
					fee,
					durationDays);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course created successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());

			return "redirect:/admin/courses/new";
		}

		return "redirect:/admin/courses";
	}
	
	@GetMapping("/admin/courses/{id}/edit")
	public String showEditForm(
			@PathVariable Long id,
			HttpSession session,
			Model model) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			Course course =
					courseService.findById(id);

			model.addAttribute(
					"course",
					course);

			model.addAttribute(
					"categories",
					categoryService.findActive());

			model.addAttribute(
					"providers",
					providerService.findActive());

			return "course-edit";

		} catch (IllegalArgumentException e) {

			return "redirect:/admin/courses";
		}
	}
	
	@PostMapping("/admin/courses/{id}")
	public String updateCourse(
			@PathVariable Long id,
			@RequestParam String code,
			@RequestParam String title,
			@RequestParam(required = false) String description,
			@RequestParam Long categoryId,
			@RequestParam Long providerId,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal fee,
			@RequestParam BigDecimal durationDays,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			courseService.update(
					id,
					code,
					title,
					description,
					categoryId,
					providerId,
					location,
					fee,
					durationDays);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course updated successfully");

			return "redirect:/admin/courses";

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());

			return "redirect:/admin/courses/"
					+ id + "/edit";
		}
	}
	
	@PostMapping("/admin/courses/{id}/deactivate")
	public String deactivateCourse(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			courseService.deactivate(id);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course deactivated successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}

		return "redirect:/admin/courses";
	}
	
	@PostMapping("/admin/courses/{id}/reactivate")
	public String reactivateCourse(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			courseService.reactivate(id);

			redirectAttributes.addFlashAttribute(
					"success",
					"Course reactivated successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}

		return "redirect:/admin/courses";
	}
}
