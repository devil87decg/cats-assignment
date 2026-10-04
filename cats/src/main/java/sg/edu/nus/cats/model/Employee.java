package sg.edu.nus.cats.model;

//import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "user_id", unique = true)
	private User user;
	private String name; 
	private String designation;
	
	@Enumerated(EnumType.STRING)
	private StaffCategory staffCategory;
	
	private String department;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supervisor_id")
	private Employee supervisor;
	
	public Employee() {
		
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Employee getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Employee supervisor) {
		this.supervisor = supervisor;
	}

	public StaffCategory getStaffCategory() {
		return staffCategory;
	}

	public void setStaffCategory(StaffCategory staffCategory) {
		this.staffCategory = staffCategory;
	}
	
	
}
