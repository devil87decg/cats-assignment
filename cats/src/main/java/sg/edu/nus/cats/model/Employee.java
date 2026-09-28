package sg.edu.nus.cats.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private BigDecimal trainingBudget;
	private BigDecimal trainingDays;
	@Enumerated(EnumType.STRING)
	private Designation designation;
	@Enumerated(EnumType.STRING)
	private Role role;
	private long supervisorId;
	private String email;
	private String password;
	

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "supervisor_id") private Employee supervisor;
	 */
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<CourseApplication> applications = new ArrayList<>();

	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<Course> courses = new ArrayList<>();

	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<CourseApplication> approved = new ArrayList<>();
	// ---------- helpers that keep both sides in sync ----------

}
