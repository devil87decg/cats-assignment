package sg.edu.nus.cats.controller;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.dto.TrainingCalendarEntryDto;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.repository.ApplicationRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.service.TrainingCalendarService;

@Controller
@AllArgsConstructor
@RequestMapping("/training-calendar")
public class TrainingCalendarController {

	private final TrainingCalendarService trainingCalendarService;
	private final EmployeeRepository employeeRepository;
	private final ApplicationRepository applicationRepository;

	@GetMapping
	public String showTrainingCalendar(@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month, Model model) {

		YearMonth selectedMonth;

		if (year == null || month == null) {
			selectedMonth = YearMonth.now();
		} else {
			selectedMonth = YearMonth.of(year, month);
		}

		model.addAttribute("calendarEntries", trainingCalendarService.getCalendarEntriesForMonth(selectedMonth));
		model.addAttribute("selectedMonth", selectedMonth);
		model.addAttribute("previousMonth", selectedMonth.minusMonths(1));
		model.addAttribute("nextMonth", selectedMonth.plusMonths(1));

		return "training-calendar";
	}

	@GetMapping("/details")
	public String showTrainingCalendarDetails(@RequestParam Long courseId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, HttpSession session,
			Model model) {

		TrainingCalendarEntryDto calendarEntry = trainingCalendarService.getCalendarEntry(courseId, startDate, endDate);

		if (calendarEntry == null) {
			return "redirect:/";
		}

		boolean courseStarted = !calendarEntry.getStartDate().isAfter(LocalDate.now());

		boolean alreadyApplied = false;

		Long loggedInUserId = (Long) session.getAttribute("userId");

		if (loggedInUserId != null) {

			Employee loggedInEmployee = employeeRepository.findByUserId(loggedInUserId).orElse(null);

			if (loggedInEmployee != null) {

				alreadyApplied = applicationRepository.existsByEmployeeIdAndCourseIdAndStartDateAndEndDate(
						loggedInEmployee.getId(), courseId, startDate, endDate);
			}
		}

		model.addAttribute("calendarEntry", calendarEntry);
		model.addAttribute("courseStarted", courseStarted);
		model.addAttribute("alreadyApplied", alreadyApplied);
		model.addAttribute("canApply", loggedInUserId != null && !courseStarted && !alreadyApplied);

		return "training-calendar-details";
	}

}
