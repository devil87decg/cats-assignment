package sg.edu.nus.cats.controller;

import java.time.YearMonth;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.cats.service.TrainingCalendarService;

@ControllerAdvice
public class TrainingCalendarAdvice {
	
	private final TrainingCalendarService trainingCalendarService;
	
	public TrainingCalendarAdvice(TrainingCalendarService trainingCalendarService) {
		this.trainingCalendarService = trainingCalendarService;
	}
	
	@ModelAttribute
	public void addTrainingCalendarData(
	        @RequestParam(required = false) Integer year,
	        @RequestParam(required = false) Integer month,
	        Model model) {

	    YearMonth selectedMonth;

	    if (year == null || month == null) {
	        selectedMonth = YearMonth.now();
	    } else {
	        selectedMonth = YearMonth.of(year, month);
	    }

	    model.addAttribute(
	        "calendarEntries",
	        trainingCalendarService.getCalendarEntriesForMonth(selectedMonth)
	    );

	    model.addAttribute("selectedMonth", selectedMonth);
	    model.addAttribute("previousMonth", selectedMonth.minusMonths(1));
	    model.addAttribute("nextMonth", selectedMonth.plusMonths(1));
	}
	
	

}
