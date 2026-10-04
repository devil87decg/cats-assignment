package sg.edu.nus.cats.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.model.PublicHoliday;
import sg.edu.nus.cats.repository.HolidayRepository;

@Service
public class HolidayService {

	private final HolidayRepository holidayRepository;

	public HolidayService(HolidayRepository holidayRepository) {
		this.holidayRepository = holidayRepository;
	}

	// Find all public holiday and order by earliest to latest
	public List<PublicHoliday> findAllHolidays() {
		return this.holidayRepository.findAllByOrderByDateAsc();
	}

	// Find holiday by id. Will give error if id (holiday) not found
	public PublicHoliday findHoliday(Long id) {
		return holidayRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Public holiday not found"));
	}
	
	// Create holiday with date and name of the holiday
	public PublicHoliday createHoliday(LocalDate date, String name) {
		
		// Check if it is valid holiday, make sure date and name is not null and name is not blank
		validateHoliday(date, name);
		
		// Ensure that there is no existance holiday on that particular date
		if(holidayRepository.existsByDate(date)) {
			throw new IllegalArgumentException("A public holiday already exists on this date");
		}
		
		// Create the public holiday instance
		PublicHoliday holiday = new PublicHoliday();
		
		// Set the date
		holiday.setDate(date);
		
		// Set the name of the holiday and trim
		holiday.setName(name.trim());
		
		// Save to database
		return holidayRepository.save(holiday);
	}
	
	// Update an existing public holiday by id, date and name
	public PublicHoliday updateHoliday(Long id, LocalDate date, String name) {
		
		// Find existing public holiday by id
		PublicHoliday holiday = findHoliday(id);
		
		// Validate holiday date and name, ensure the date and name are not null or blank
		validateHoliday(date, name);
		
		// Make sure no other public holiday is using the same date
		if(holidayRepository.existsByDateAndIdNot(date, id)) {
			throw new IllegalArgumentException("A public holiday already exits on this date");
		}
		
		// Update the existing holiday
		holiday.setDate(date);
		holiday.setName(name.trim());
		
		// Save the changes
		return holidayRepository.save(holiday);
		
	}
	
	// Delete public holiday
	public void deleteHoliday(Long id) {
		
		// If public holiday does not exist by id, throw an error not found
		if(!holidayRepository.existsById(id)) {
			throw new IllegalArgumentException("Public holiday not found");
		}
		
		// Else delete public holiday by id
		holidayRepository.deleteById(id);
	}
	
	// Check if the date and id is valid or not.
	// Check date is not null
	// Check name is not null or blank
	public void validateHoliday(LocalDate date, String name) {
		if(date == null) {
			throw new IllegalArgumentException("Holiday date is required");
		}
		
		if(name == null || name.isBlank()) {			
			throw new IllegalArgumentException("Holiday name is required");
		}
	}

}
