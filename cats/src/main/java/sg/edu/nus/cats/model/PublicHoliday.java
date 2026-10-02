package sg.edu.nus.cats.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PublicHoliday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// date holds a calendar date such as 2026-12-25.
	// unique = true -> tells MySQL that two holiday rows cannot use the same date.
	@Column(unique = true)
	private LocalDate date;
	
	// name makes the holiday understandable when an admin views the list.
	private String name;
	
	public PublicHoliday() {
		
	}

	public Long getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
