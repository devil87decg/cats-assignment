package sg.edu.nus.cats.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

@Data
@NoArgsConstructor
@Entity
public class CourseApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// Employee holds the staff member who submitted the application.
	// @ManytoOne means one employee can submit many applications.
	// optional = false means every application needs an applicant 
	@ManyToOne(optional = false)
	private Employee employee;
	
	private String courseTitle;
	
	@Enumerated(EnumType.STRING)
	private CourseCategory category;
	
	private String trainingProvider;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate;
	
	private BigDecimal durationDays;
	private BigDecimal fee;
	
	private boolean halfDay;
	private String justification;
	private String workDissemination;
	
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status = ApplicationStatus.APPLIED;
	
	private String managerReason;
	private String experienceComment;
	private LocalDateTime decisionDate;
	
	// ManyToOne as One manager may decide many course applications.
	// No optional = false here -> decidedBy may remain empty while no decision has been made.
	@ManyToOne
	private Employee decidedBy;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;
		
	
}