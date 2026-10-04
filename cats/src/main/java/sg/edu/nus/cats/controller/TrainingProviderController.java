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
import sg.edu.nus.cats.service.TrainingProviderService;
import sg.edu.nus.cats.utils.AdminAuthHelper;

@AllArgsConstructor
@Controller
public class TrainingProviderController {
	private final TrainingProviderService providerService;
	private final UserRepository users;
	private final AdminAuthHelper adminAuth;
	
	@GetMapping("/admin/training-providers")
	public String showProviders(
			HttpSession session,
			Model model) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		model.addAttribute(
				"providers",
				providerService.findAll());

		return "training-provider-list";
	}


	@GetMapping("/admin/training-providers/new")
	public String showCreateForm(
			HttpSession session) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		return "training-provider-form";
	}


	@PostMapping("/admin/training-providers")
	public String createProvider(
			@RequestParam String name,
			@RequestParam(required = false) String description,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			providerService.create(
					name,
					description);

			redirectAttributes.addFlashAttribute(
					"success",
					"Training provider created successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());

			return "redirect:/admin/training-providers/new";
		}

		return "redirect:/admin/training-providers";
	}


	@GetMapping("/admin/training-providers/{id}/edit")
	public String showEditForm(
			@PathVariable Long id,
			HttpSession session,
			Model model) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			model.addAttribute(
					"provider",
					providerService.findById(id));

			return "training-provider-edit";

		} catch (IllegalArgumentException e) {

			return "redirect:/admin/training-providers";
		}
	}


	@PostMapping("/admin/training-providers/{id}")
	public String updateProvider(
			@PathVariable Long id,
			@RequestParam String name,
			@RequestParam(required = false) String description,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			providerService.update(
					id,
					name,
					description);

			redirectAttributes.addFlashAttribute(
					"success",
					"Training provider updated successfully");

			return "redirect:/admin/training-providers";

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());

			return "redirect:/admin/training-providers/"
					+ id + "/edit";
		}
	}


	@PostMapping("/admin/training-providers/{id}/deactivate")
	public String deactivateProvider(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			providerService.deactivate(id);

			redirectAttributes.addFlashAttribute(
					"success",
					"Training provider deactivated successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}

		return "redirect:/admin/training-providers";
	}


	@PostMapping("/admin/training-providers/{id}/reactivate")
	public String reactivateProvider(
			@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User admin = adminAuth.getAdmin(session);

		if (admin == null) {
			return "redirect:/";
		}

		try {

			providerService.reactivate(id);

			redirectAttributes.addFlashAttribute(
					"success",
					"Training provider reactivated successfully");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute(
					"error",
					e.getMessage());
		}

		return "redirect:/admin/training-providers";
	}

}
