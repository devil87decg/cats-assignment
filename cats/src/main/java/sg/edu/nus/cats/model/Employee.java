package sg.edu.nus.cats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "employees")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", nullable = false, length = 50)
	@NotBlank(message = "First name is required")
	private String firstName;
	
	@Column(name = "last_name", nullable = false, length = 50)
	@NotBlank(message = "Last name is required")
	private String lastName;
	
	@Column(name = "contact_number", nullable = false, length = 15)
	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^\\+65?[0-9]{8}$", message = "Contact number must be valid Singapore Number") 
	private String contactNumber;
}
