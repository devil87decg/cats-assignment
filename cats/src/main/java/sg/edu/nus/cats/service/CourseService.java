package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.Course;
import sg.edu.nus.cats.model.CourseCategoryMaster;
import sg.edu.nus.cats.model.TrainingProvider;
import sg.edu.nus.cats.repository.ApplicationRepository;
import sg.edu.nus.cats.repository.CourseCategoryRepository;
import sg.edu.nus.cats.repository.CourseRepository;
import sg.edu.nus.cats.repository.TrainingProviderRepository;

@AllArgsConstructor
@Service
public class CourseService {
	private final CourseRepository courses;
	private final CourseCategoryRepository categories;
	private final TrainingProviderRepository providers;
	private final ApplicationRepository applications;
	
	public List<Course> findAll() {
		return courses.findAll();
	}


	public List<Course> findActive() {
		return courses.findByActiveTrue();
	}


	public Course findById(Long id) {

		return courses.findById(id)
				.orElseThrow(() ->
					new IllegalArgumentException(
							"Course not found"));
	}


	public Course create(
			String code,
			String title,
			String description,
			Long categoryId,
			Long providerId,
			String location,
			BigDecimal fee,
			BigDecimal durationDays) {

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException(
					"Course code is required");
		}

		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException(
					"Course title is required");
		}

		if (categoryId == null) {
			throw new IllegalArgumentException(
					"Course category is required");
		}

		if (providerId == null) {
			throw new IllegalArgumentException(
					"Training provider is required");
		}

		BigDecimal halfDay = new BigDecimal("0.5");

		if (durationDays.compareTo(halfDay) < 0) {
			throw new IllegalArgumentException(
					"Course duration must be at least half a day");
		}
		
		if (durationDays.remainder(halfDay)
				.compareTo(BigDecimal.ZERO) != 0) {

			throw new IllegalArgumentException(
					"Course duration must be in half-day increments");
		}
		

		String cleanCode = code.trim().toUpperCase();
		String cleanTitle = title.trim();


		if (courses.findByCode(cleanCode).isPresent()) {

			throw new IllegalArgumentException(
					"Course code already exists");
		}


		CourseCategoryMaster category =
				categories.findById(categoryId)
				.orElseThrow(() ->
					new IllegalArgumentException(
							"Course category not found"));


		TrainingProvider provider =
				providers.findById(providerId)
				.orElseThrow(() ->
					new IllegalArgumentException(
							"Training provider not found"));


		if (!category.isActive()) {
			throw new IllegalArgumentException(
					"Selected course category is inactive");
		}


		if (!provider.isActive()) {
			throw new IllegalArgumentException(
					"Selected training provider is inactive");
		}
		
		if (durationDays.compareTo(halfDay) == 0
				&& !category.isInternalTraining()) {

			throw new IllegalArgumentException(
					"Half-day courses are allowed for internal training only");
		}

		if (fee == null) {
			fee = BigDecimal.ZERO;
		}

		if (fee.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(
					"Course fee cannot be negative");
		}


		// Internal training is free.
		if (category.isInternalTraining()) {
			fee = BigDecimal.ZERO;
		}


		Course course = new Course();

		course.setCode(cleanCode);
		course.setTitle(cleanTitle);
		course.setDescription(description);
		course.setCategory(category);
		course.setProvider(provider);
		course.setLocation(location);
		course.setFee(fee);
		course.setDurationDays(durationDays);
		course.setActive(true);

		return courses.save(course);
	}
	
	public Course update(
			Long id,
			String code,
			String title,
			String description,
			Long categoryId,
			Long providerId,
			String location,
			BigDecimal fee,
			BigDecimal durationDays) {

		Course course = findById(id);

		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException(
					"Course code is required");
		}

		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException(
					"Course title is required");
		}

		if (categoryId == null) {
			throw new IllegalArgumentException(
					"Course category is required");
		}

		if (providerId == null) {
			throw new IllegalArgumentException(
					"Training provider is required");
		}

		if (durationDays == null) {
			throw new IllegalArgumentException(
					"Course duration is required");
		}


		BigDecimal halfDay = new BigDecimal("0.5");

		if (durationDays.compareTo(halfDay) < 0) {
			throw new IllegalArgumentException(
					"Course duration must be at least half a day");
		}

		if (durationDays.remainder(halfDay)
				.compareTo(BigDecimal.ZERO) != 0) {

			throw new IllegalArgumentException(
					"Course duration must be in half-day increments");
		}


		String cleanCode =
				code.trim().toUpperCase();

		String cleanTitle =
				title.trim();


		// Another course cannot use this code.
		courses.findByCode(cleanCode)
				.ifPresent(existing -> {

					if (!existing.getId().equals(id)) {
						throw new IllegalArgumentException(
								"Course code already exists");
					}
				});


		CourseCategoryMaster category =
				categories.findById(categoryId)
						.orElseThrow(() ->
							new IllegalArgumentException(
									"Course category not found"));


		TrainingProvider provider =
				providers.findById(providerId)
						.orElseThrow(() ->
							new IllegalArgumentException(
									"Training provider not found"));


		if (!category.isActive()) {
			throw new IllegalArgumentException(
					"Selected course category is inactive");
		}

		if (!provider.isActive()) {
			throw new IllegalArgumentException(
					"Selected training provider is inactive");
		}


		if (durationDays.compareTo(halfDay) == 0
				&& !category.isInternalTraining()) {

			throw new IllegalArgumentException(
					"Half-day courses are allowed for internal training only");
		}


		if (fee == null) {
			fee = BigDecimal.ZERO;
		}

		if (fee.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(
					"Course fee cannot be negative");
		}

		if (category.isInternalTraining()) {
			fee = BigDecimal.ZERO;
		}


		course.setCode(cleanCode);
		course.setTitle(cleanTitle);
		course.setDescription(description);
		course.setCategory(category);
		course.setProvider(provider);
		course.setLocation(location);
		course.setFee(fee);
		course.setDurationDays(durationDays);

		return courses.save(course);
	}
	
	public void deactivate(Long id) {

		Course course = findById(id);

		if (!course.isActive()) {
			throw new IllegalArgumentException(
					"Course is already inactive");
		}

		course.setActive(false);

		courses.save(course);
	}
	
	public void reactivate(Long id) {

		Course course = findById(id);

		if (course.isActive()) {
			throw new IllegalArgumentException(
					"Course is already active");
		}

		/*
		 * Don't reactivate a course whose category
		 * or provider has itself been deactivated.
		 */
		if (!course.getCategory().isActive()) {
			throw new IllegalArgumentException(
					"Cannot reactivate course because its category is inactive");
		}

		if (!course.getProvider().isActive()) {
			throw new IllegalArgumentException(
					"Cannot reactivate course because its training provider is inactive");
		}

		course.setActive(true);

		courses.save(course);
	}
	
	@Transactional
	public boolean delete(Long id) {

		Course course = findById(id);

		// Course already has application/history.
		// Preserve it and deactivate instead.
		if (applications.existsByCourseId(id)) {

			course.setActive(false);
			courses.save(course);

			return false;
		}

		// Never used: safe to physically delete.
		courses.delete(course);

		return true;
	}
}
