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
import jakarta.persistence.ManyToOne;

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
	
	public CourseApplication() {
	
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public CourseCategory getCategory() {
		return category;
	}

	public void setCategory(CourseCategory category) {
		this.category = category;
	}

	public String getTrainingProvider() {
		return trainingProvider;
	}

	public void setTrainingProvider(String trainingProvider) {
		this.trainingProvider = trainingProvider;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getDurationDays() {
		return durationDays;
	}

	public void setDurationDays(BigDecimal durationDays) {
		this.durationDays = durationDays;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public boolean isHalfDay() {
		return halfDay;
	}

	public void setHalfDay(boolean halfDay) {
		this.halfDay = halfDay;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public String getWorkDissemination() {
		return workDissemination;
	}

	public void setWorkDissemination(String workDissemination) {
		this.workDissemination = workDissemination;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	public String getManagerReason() {
		return managerReason;
	}

	public void setManagerReason(String managerReason) {
		this.managerReason = managerReason;
	}

	public String getExperienceComment() {
		return experienceComment;
	}

	public void setExperienceComment(String experienceComment) {
		this.experienceComment = experienceComment;
	}

	public LocalDateTime getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(LocalDateTime decisionDate) {
		this.decisionDate = decisionDate;
	}

	public Employee getDecidedBy() {
		return decidedBy;
	}

	public void setDecidedBy(Employee decidedBy) {
		this.decidedBy = decidedBy;
	}
	
	
}