package sg.edu.nus.cats.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import sg.edu.nus.cats.dto.TrainingCalendarEntryDto;
import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.repository.ApplicationRepository;

@Service
@AllArgsConstructor
public class TrainingCalendarService {

	private final ApplicationRepository applicationRepository;

	public List<TrainingCalendarEntryDto> getCalendarEntriesForMonth(YearMonth month) {
		LocalDate monthStart = month.atDay(1);
		LocalDate monthEnd = month.atEndOfMonth();

		List<CourseApplication> approvedApplications = applicationRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
				ApplicationStatus.APPROVED, monthEnd, monthStart);
		
		List<TrainingCalendarEntryDto> calendarEntries = new ArrayList<>();
		
		for(CourseApplication application : approvedApplications) {
			TrainingCalendarEntryDto matchingEntry = null;

			// Loop throught the calendar training courses
			for (TrainingCalendarEntryDto entry : calendarEntries) {

				if (entry.getCourse().getId().equals(application.getCourse().getId())
						&& entry.getStartDate().equals(application.getStartDate())
						&& entry.getEndDate().equals(application.getEndDate())) {

					matchingEntry = entry;
					break;
				}
			}

			if (matchingEntry == null) {

				matchingEntry = new TrainingCalendarEntryDto();

				matchingEntry.setCourse(application.getCourse());
				matchingEntry.setStartDate(application.getStartDate());
				matchingEntry.setEndDate(application.getEndDate());

				calendarEntries.add(matchingEntry);
			}

			matchingEntry.getApplications().add(application);
		}

		return calendarEntries;
	}
	
	public TrainingCalendarEntryDto getCalendarEntry(
			Long courseId,
			LocalDate startDate,
			LocalDate endDate) {

		YearMonth month = YearMonth.from(startDate);

		List<TrainingCalendarEntryDto> calendarEntries =
				getCalendarEntriesForMonth(month);

		for (TrainingCalendarEntryDto entry : calendarEntries) {

			if (entry.getCourse().getId().equals(courseId)
					&& entry.getStartDate().equals(startDate)
					&& entry.getEndDate().equals(endDate)) {

				return entry;
			}
		}

		return null;
	}

}
