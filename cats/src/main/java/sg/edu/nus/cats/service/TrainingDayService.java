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
	public BigDecimal count(LocalDate start, LocalDate end, boolean halfDay) {
		
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
		
		// if start and end dates are different, the request is invalid 
		if (halfDay && !start.equals(end)) {
			
			throw new IllegalArgumentException("Half-day must be on one date");
		}
		// if half day requested, check whether date counted as exactly one working day
		// if selected date is weekend or public holiday, loop leaves total at 0
		// comparing 0 with 1 gives a negative result, which is !=0 -> rejects half day request
		if (halfDay && total.compareTo(BigDecimal.ONE) !=0) {
			
			throw new IllegalArgumentException("Half-day must be on a working day");
		}
		
		// request passed both checks, return half a training day
		if (halfDay) {
			
			return new BigDecimal("0.5");
		}
		
		return total;
		
	}
	
}
