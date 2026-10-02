package sg.edu.nus.cats.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
// tells JPA to request a database rule that prevents two rows with the same employee_id and year
@Table(
		uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "year"})
)
public class TrainingAllowance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// @ManyToOne allows one employee to have multiple allowance records—for example, one for 2026 and another for 2027.
	@ManyToOne(optional = false)
	private Employee employee;
	// year = 2026 means this allowance applies to that employee’s training in 2026.
	private int year;
	
	// dayLimit stores how many training days the employee is allowed for that year.
	private BigDecimal dayLimit;
	
	// feeBudget stores how much the employee may spend on eligible course fees for that year.
	private BigDecimal feeBudget;
	
	// If MySQL has a row for Alice's 2026 allowance. JPA does the below:
	// Create an empty TrainingAllowance object using TrainingAllowance()
	// Fill its fields from the row: id, employee, year, dayLimit and feeBudget.
	public TrainingAllowance() {
		
	}

	public Long getId() {
		return id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public BigDecimal getDayLimit() {
		return dayLimit;
	}

	public void setDayLimit(BigDecimal dayLimit) {
		this.dayLimit = dayLimit;
	}

	public BigDecimal getFeeBudget() {
		return feeBudget;
	}

	public void setFeeBudget(BigDecimal feeBudget) {
		this.feeBudget = feeBudget;
	}
	
	
}
