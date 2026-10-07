package sg.edu.nus.cats.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.dto.TrainingBudgetReportDto;
import sg.edu.nus.cats.model.CourseApplication;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.service.CourseCategoryService;
import sg.edu.nus.cats.service.ReportService;

@AllArgsConstructor
@Controller
public class ReportController {
	private final ReportService reportService;
	private final EmployeeRepository employees;
	private final CourseCategoryService categoryService;
	
	@GetMapping("/reports")
	public String showReports(
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate startDate,

			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate endDate,
			
			@RequestParam(required = false)
			Long categoryId,
			
			@RequestParam(required = false)
			Integer budgetYear,

			@RequestParam(required = false)
			Long employeeId,
			
			@RequestParam(required = false)
			String reportType,

			HttpSession session,
			Model model) {

		Employee manager = getManager(session);

		if (manager == null) {
			return "redirect:/";
		}

		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		
		model.addAttribute(
		        "categories",
		        categoryService.findActive());
		
		model.addAttribute("categoryId", categoryId);
		
		model.addAttribute(
				"employees",
				employees.findAll());

		model.addAttribute("budgetYear", budgetYear);
		model.addAttribute("employeeId",employeeId);
		model.addAttribute("reportType", reportType);

		if ("attendance".equals(reportType)
				&& startDate != null
				&& endDate != null) {

			try {

				List<CourseApplication> results =
						reportService.getApprovedCourseReport(
								startDate,
								endDate,
								categoryId);

				model.addAttribute(
						"results",
						results);

			} catch (IllegalArgumentException e) {

				model.addAttribute(
						"error",
						e.getMessage());
			}
		}
		
		if ("budget".equals(reportType)
				&& budgetYear != null) {

			try {

				model.addAttribute(
						"budgetResults",
						reportService.getTrainingBudgetReport(
								budgetYear,
								employeeId));

			} catch (IllegalArgumentException e) {

				model.addAttribute(
						"error",
						e.getMessage());
			}
		}

		return "reports";
	}


	private Employee getManager(HttpSession session) {

		Long employeeId =
				(Long) session.getAttribute("userId");

		if (employeeId == null) {
			return null;
		}

		Employee employee =
				employees.findById(employeeId)
						.orElse(null);

		if (employee == null
				|| employee.getUser() == null
				|| employee.getUser().getRole() != Role.MANAGER) {

			return null;
		}

		return employee;
	}
	
	@GetMapping("/reports/export")
	public void exportCourseAttendanceReport(
			@RequestParam
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate startDate,

			@RequestParam
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate endDate,

			@RequestParam(required = false)
			Long categoryId,

			HttpSession session,
			HttpServletResponse response)
			throws IOException {

		Employee manager = getManager(session);

		if (manager == null) {
			response.sendRedirect("/");
			return;
		}

		List<CourseApplication> results =
				reportService.getApprovedCourseReport(
						startDate,
						endDate,
						categoryId);

		response.setContentType("text/csv");
		response.setCharacterEncoding("UTF-8");

		String filename =
				"course-attendance-"
				+ startDate
				+ "-to-"
				+ endDate
				+ ".csv";

		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + filename + "\"");

		PrintWriter writer = response.getWriter();

		writer.println(
				"Employee,Course,Category,Provider,"
				+ "Start Date,End Date,Training Days,Fee (S$)");

		for (CourseApplication courseRequest : results) {

			writer.println(
					csv(courseRequest.getEmployee().getName()) + ","
					+ csv(courseRequest.getCourseTitle()) + ","
					+ csv(courseRequest.getCourse()
							.getCategory().getName()) + ","
					+ csv(courseRequest.getTrainingProvider()) + ","
					+ csv(courseRequest.getStartDate().toString()) + ","
					+ csv(courseRequest.getEndDate().toString()) + ","
					+ csv(courseRequest.getDurationDays().toString()) + ","
					+ csv(courseRequest.getFee().toString()));
		}

		writer.flush();
	}
	
	@GetMapping("/reports/budget/export")
	public void exportTrainingBudgetReport(
			@RequestParam int budgetYear,
			@RequestParam(required = false) Long employeeId,
			HttpSession session,
			HttpServletResponse response)
			throws IOException {

		Employee manager = getManager(session);

		if (manager == null) {
			response.sendRedirect("/");
			return;
		}

		List<TrainingBudgetReportDto> results =
				reportService.getTrainingBudgetReport(
						budgetYear,
						employeeId);

		String filename =
				"training-budget-utilisation-"
				+ budgetYear
				+ ".csv";

		response.setContentType("text/csv");
		response.setCharacterEncoding("UTF-8");

		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + filename + "\"");

		PrintWriter writer = response.getWriter();

		writer.println(
				"Employee,Year,Day Allowance,Days Used,"
				+ "Days Remaining,Fee Budget (S$),"
				+ "Budget Used (S$),Budget Remaining (S$)");

		for (TrainingBudgetReportDto row : results) {

			writer.println(
					csv(row.getEmployeeName()) + ","
					+ csv(String.valueOf(row.getYear())) + ","
					+ csv(row.getDayAllowance().toString()) + ","
					+ csv(row.getDaysUsed().toString()) + ","
					+ csv(row.getDaysRemaining().toString()) + ","
					+ csv(row.getFeeBudget().toString()) + ","
					+ csv(row.getBudgetUsed().toString()) + ","
					+ csv(row.getBudgetRemaining().toString()));
		}

		writer.flush();
	}
	
	private String csv(String value) {

		if (value == null) {
			return "";
		}

		return "\""
				+ value.replace("\"", "\"\"")
				+ "\"";
	}
}
