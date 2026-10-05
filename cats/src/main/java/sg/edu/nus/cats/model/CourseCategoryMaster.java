package sg.edu.nus.cats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "course_categories")
public class CourseCategoryMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String code;

	@Column(nullable = false, unique = true)
	private String name;

	private String description;

	// Determines the business behaviour of this category.
	private boolean internalTraining;

	// Inactive categories remain for historical records
	// but should not be available for new courses.
	private boolean active = true;

}
