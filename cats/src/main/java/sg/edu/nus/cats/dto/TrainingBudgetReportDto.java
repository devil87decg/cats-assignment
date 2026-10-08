package sg.edu.nus.cats.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TrainingBudgetReportDto {
	private Long employeeId;
	private String employeeName;
	private int year;

	private BigDecimal dayAllowance;
	private BigDecimal daysUsed;
	private BigDecimal daysRemaining;

	private BigDecimal feeBudget;
	private BigDecimal budgetUsed;
	private BigDecimal budgetRemaining;
}
