package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import sg.edu.nus.cats.dto.TrainingBudgetReportDto;
import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.TrainingAllowance;
import sg.edu.nus.cats.repository.AllowanceRepository;
import sg.edu.nus.cats.repository.ApplicationRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;

@AllArgsConstructor
@Service
public class ReportService {
	private final ApplicationRepository applications;
	private final EmployeeRepository employees;
	private final AllowanceRepository allowances;
	private final ApplicationService applicationService;
	
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
	
	public List<TrainingBudgetReportDto> getTrainingBudgetReport(
			int year,
			Long employeeId) {

		List<Employee> employeeList;

		if (employeeId == null) {

			employeeList = employees.findAll();

		} else {

			Employee employee =
					employees.findById(employeeId)
							.orElseThrow(() ->
									new IllegalArgumentException(
											"Employee not found"));

			employeeList = List.of(employee);
		}


		List<TrainingBudgetReportDto> results =
				new ArrayList<>();


		for (Employee employee : employeeList) {

			Optional<TrainingAllowance> allowanceResult =
					allowances.findByEmployeeIdAndYear(
							employee.getId(),
							year);

			/*
			 * No allowance for this employee/year:
			 * skip the employee from the utilisation report.
			 */
			if (allowanceResult.isEmpty()) {
				continue;
			}


			TrainingAllowance allowance =
					allowanceResult.get();


			BigDecimal dayAllowance =
					allowance.getDayLimit() != null
							? allowance.getDayLimit()
							: BigDecimal.ZERO;

			BigDecimal feeBudget =
					allowance.getFeeBudget() != null
							? allowance.getFeeBudget()
							: BigDecimal.ZERO;


			BigDecimal daysUsed =
					applicationService.calculateUsedDays(
							employee,
							year);

			BigDecimal budgetUsed =
					applicationService.calculateUsedFees(
							employee,
							year);


			TrainingBudgetReportDto row =
					new TrainingBudgetReportDto();

			row.setEmployeeId(employee.getId());
			row.setEmployeeName(employee.getName());
			row.setYear(year);

			row.setDayAllowance(dayAllowance);
			row.setDaysUsed(daysUsed);
			row.setDaysRemaining(
					dayAllowance.subtract(daysUsed));

			row.setFeeBudget(feeBudget);
			row.setBudgetUsed(budgetUsed);
			row.setBudgetRemaining(
					feeBudget.subtract(budgetUsed));

			results.add(row);
		}


		return results;
	}
}
