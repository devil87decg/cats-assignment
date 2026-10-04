package sg.edu.nus.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import sg.edu.nus.cats.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
	Optional<Course> findByCode(String code);

	List<Course> findByActiveTrue();

	boolean existsByCategoryId(Long categoryId);

	boolean existsByProviderId(Long providerId);
}
