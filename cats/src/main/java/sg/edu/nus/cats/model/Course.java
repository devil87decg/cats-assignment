package sg.edu.nus.cats.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String courseName;
	private String trainingProvider;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate endDate;
	private BigDecimal fees;
	@Enumerated(EnumType.STRING)
	private CourseType type;

	@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
	private List<CourseApplication> events = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

}
