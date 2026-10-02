package sg.edu.nus.cats.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.PublicHoliday;

// PublicHoliday is the type of entity this repository handles
// Long is the type of that entity's primary key
public interface HolidayRepository extends JpaRepository<PublicHoliday, Long> {

	// Does a PublicHoliday row exist with this date?
	// Spring Data Jpa creates the lookup 
	// it returns true if date is recorded as holiday, or false if it is not.
	boolean existsByDate(LocalDate date);
	
}
