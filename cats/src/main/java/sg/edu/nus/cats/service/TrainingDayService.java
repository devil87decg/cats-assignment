package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.repository.HolidayRepository;

@Service
public class TrainingDayService {

	private final HolidayRepository holidays;
	
	public TrainingDayService(HolidayRepository holidays) {
		
		this.holidays = holidays;
		
	}
	
	// start and end are the course dates to count.
	// halfDay records whether the application requests a half day
	// BigDecimal is the type of result: the number of training days
	// total starts at zero
	public BigDecimal count(LocalDate start, LocalDate end) {
		
		// basically course start date cannot be after end date
		if (start.isAfter(end)) {
			
			throw new IllegalArgumentException("Start date must not be after end date");
			
		}
		
		// LocalDate d = start -> begin on the course's start date
		// !d.isAfter(end) -> keep going while d is on or before the end date
		// d = d.plusDays(1) -> move to the next date after each pass
		// Together: “Check this date, move forward one day, and stop once we pass the course end date.”
		BigDecimal total = BigDecimal.ZERO;
		
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			// getDayOfWeek().getValue() gives a number from 1 (Monday) to 7 (Sunday)
			// >= matches both Sat (6) and Sun (7), after Sun (7), the next day is Monday (1) again,
			// as getDayOfWeek() gives the day within one week
			boolean weekend = date.getDayOfWeek().getValue() >= 6;
			// if this date is not a weekend AND is not recorded as a public holiday, add to total
			if (!weekend && !holidays.existsByDate(date)) {
				total = total.add(BigDecimal.ONE);
			}
			
		}	
		
		return total;
		
	}
	
	public LocalDate calculateEndDate(LocalDate start, BigDecimal duration) {
		if (start == null) {
	        throw new IllegalArgumentException("Start date is required");
	    }

	    if (duration == null || duration.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Course duration must be greater than zero");
	    }

	    // The course must start on a working day.
	    if (!isWorkingDay(start)) {
	        throw new IllegalArgumentException(
	                "Course start date must be a working day");
	    }

	    // A half-day course starts and ends on the same working day.
	    if (duration.compareTo(new BigDecimal("0.5")) == 0) {
	        return start;
	    }

	    LocalDate currentDate = start;
	    BigDecimal countedDays = BigDecimal.ONE;

	    while (countedDays.compareTo(duration) < 0) {

	        currentDate = currentDate.plusDays(1);

	        if (isWorkingDay(currentDate)) {
	            countedDays = countedDays.add(BigDecimal.ONE);
	        }
	    }

	    return currentDate;
	}
	
	// check if the date is working day
	private boolean isWorkingDay(LocalDate date) {

	    boolean weekend =
	            date.getDayOfWeek().getValue() >= 6;

	    return !weekend && !holidays.existsByDate(date);
	}
	
}
