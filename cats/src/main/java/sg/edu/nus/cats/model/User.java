package sg.edu.nus.cats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Entity tells JPA: "Objects of this class can be saved as rows in a database table".
@Entity
@Table(name = "users")
public class User {
	
	// Marks id as the row's primary key.
	@Id
	// Lets MySQL generate a new ID for each saved user.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// nullable = false -> a saved users row must have a username.
	// unique = true -> two saved username cannot have the same username.
	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String passwordHash;
	
	// Tells CATs whether the account belongs to an employee, manager or admin.
	// Stores the role by name, such as manager, rather than a number.
	@Enumerated(EnumType.STRING)
	private Role role;
	
	// New Java user objects begin as active.
	// Records whether the account is active.
	private boolean active = true;
	
	public User() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
