package sg.edu.nus.cats.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String code;

	@Column(nullable = false)
	private String title;

	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private CourseCategoryMaster category;

	@ManyToOne(optional = false)
	@JoinColumn(name = "provider_id")
	private TrainingProvider provider;

	private String location;

	private BigDecimal fee;

	private BigDecimal durationDays;

	private boolean internalHalfDay;

	private boolean active = true;
}
