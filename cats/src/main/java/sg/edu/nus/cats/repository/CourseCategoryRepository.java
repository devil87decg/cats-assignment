package sg.edu.nus.cats.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.CourseCategoryMaster;

public interface CourseCategoryRepository extends JpaRepository<CourseCategoryMaster, Long>{
	Optional<CourseCategoryMaster> findByCode(String code);

	Optional<CourseCategoryMaster> findByName(String name);

	List<CourseCategoryMaster> findByActiveTrue();
}
