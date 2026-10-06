package sg.edu.nus.cats.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sg.edu.nus.cats.model.Course;
import sg.edu.nus.cats.model.CourseApplication;

@Data
public class TrainingCalendarEntryDto {
	
	private Course course;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<CourseApplication> applications = new ArrayList<>();

}
