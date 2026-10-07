package sg.edu.nus.cats.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.repository.ApplicationRepository;

@AllArgsConstructor
@Service
public class ReportService {
	private final ApplicationRepository applications;
	
	public List<CourseApplication> getApprovedCourseReport(
			LocalDate startDate,
			LocalDate endDate,
			Long categoryId) {

		if (startDate == null || endDate == null) {
			throw new IllegalArgumentException(
					"Start date and end date are required");
		}

		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException(
					"End date must not be before start date");
		}

		List<CourseApplication> results = applications
				.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
						ApplicationStatus.APPROVED,
						endDate,
						startDate);
		
		if (categoryId == null) {
		    return results;
		}

		return results.stream()
		        .filter(courseRequest ->
		                courseRequest.getCourse() != null
		                && courseRequest.getCourse().getCategory() != null
		                && courseRequest.getCourse()
		                        .getCategory()
		                        .getId()
		                        .equals(categoryId))
		        .toList();
	}
}
