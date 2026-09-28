package sg.edu.nus.cats.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CourseApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate applicationDate;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate decisionDate;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate lastUpdatedAt;
	private String empJustification;
	private String workDissemination;
	private String mgrReason;
	private String experienceComments;
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "decided_by")
	private Employee decidedBy;
}
