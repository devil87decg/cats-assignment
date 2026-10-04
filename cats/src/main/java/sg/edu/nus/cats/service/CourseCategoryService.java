package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.CourseCategoryMaster;
import sg.edu.nus.cats.repository.CourseCategoryRepository;

@Service
@AllArgsConstructor
public class CourseCategoryService {
	private final CourseCategoryRepository categories;
	
	public List<CourseCategoryMaster> findAll() {
		return categories.findAll();
	}


	public List<CourseCategoryMaster> findActive() {
		return categories.findByActiveTrue();
	}
	
	public CourseCategoryMaster findById(Long id) {

		return categories.findById(id)
				.orElseThrow(() ->
					new IllegalArgumentException(
						"Course category not found"));
	}
	
	public CourseCategoryMaster create(
			String code,
			String name,
			String description,
			boolean internalTraining) {

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException(
					"Category code is required");
		}

		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException(
					"Category name is required");
		}

		String cleanCode =
				code.trim().toUpperCase();

		String cleanName =
				name.trim();


		if (categories.findByCode(cleanCode).isPresent()) {

			throw new IllegalArgumentException(
					"Category code already exists");
		}


		if (categories.findByName(cleanName).isPresent()) {

			throw new IllegalArgumentException(
					"Category name already exists");
		}


		CourseCategoryMaster category =
				new CourseCategoryMaster();

		category.setCode(cleanCode);
		category.setName(cleanName);
		category.setDescription(description);
		category.setInternalTraining(internalTraining);
		category.setActive(true);

		return categories.save(category);
	}
	
	public CourseCategoryMaster update(
			Long id,
			String code,
			String name,
			String description,
			boolean internalTraining) {

		CourseCategoryMaster category = findById(id);

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException(
					"Category code is required");
		}

		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException(
					"Category name is required");
		}

		String cleanCode = code.trim().toUpperCase();
		String cleanName = name.trim();


		// Check whether another category already uses this code
		categories.findByCode(cleanCode)
				.ifPresent(existing -> {

					if (!existing.getId().equals(id)) {
						throw new IllegalArgumentException(
								"Category code already exists");
					}
				});


		// Check whether another category already uses this name
		categories.findByName(cleanName)
				.ifPresent(existing -> {

					if (!existing.getId().equals(id)) {
						throw new IllegalArgumentException(
								"Category name already exists");
					}
				});


		category.setCode(cleanCode);
		category.setName(cleanName);
		category.setDescription(description);
		category.setInternalTraining(internalTraining);

		return categories.save(category);
	}
	
	public void deactivate(Long id) {

		CourseCategoryMaster category = findById(id);

		if (!category.isActive()) {
			throw new IllegalArgumentException(
					"Course category is already inactive");
		}

		category.setActive(false);

		categories.save(category);
	}
	
	public void reactivate(Long id) {

		CourseCategoryMaster category = findById(id);

		if (category.isActive()) {
			throw new IllegalArgumentException(
					"Course category is already active");
		}

		category.setActive(true);

		categories.save(category);
	}
}
