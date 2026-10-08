package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.StaffCategory;
import sg.edu.nus.cats.model.TrainingAllowance;
import sg.edu.nus.cats.repository.AllowanceRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;

@Service
public class AllowanceService {
	
	private final EmployeeRepository employees;
	
	private final AllowanceRepository allowances;

	public AllowanceService(EmployeeRepository employees, AllowanceRepository allowances) {
		
		this.employees = employees;
		this.allowances = allowances;
	}
	
	// Only admin able to set the allowance to override the default allowance
	public TrainingAllowance setAllowance(Long employeeId, int year, BigDecimal dayLimit, BigDecimal feeBudget) {
		
		if (year <2000) {
			
			throw new IllegalArgumentException("Enter a valid allowance year");
		}
		
		// if no day limit was entered, or the limit is zero or negative -> throw an error
		if (dayLimit == null || dayLimit.compareTo(BigDecimal.ZERO) <= 0) {
			
			throw new IllegalArgumentException("Training-day limit must be greater than zero");
		}
		
		// if fee budget was entered and it is below zero -> throw an error
		if (feeBudget != null && feeBudget.compareTo(BigDecimal.ZERO) < 0) {
			
			throw new IllegalArgumentException("Fee budget cannot be negative");
		}
		
		// find the employee who will receive this allowance
		Employee employee = employees.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));
		
		// require a staff category before deciding the training-day allowance
		if (employee.getStaffCategory() == null) {
			
			throw new IllegalArgumentException("Set the employee's staff category first");
		}
			
		// ask AllowanceRepository for this employee's allowance for this year
		Optional<TrainingAllowance> existingAllowance = allowances.findByEmployeeIdAndYear(employeeId, year);
		
		TrainingAllowance allowance;
		// if employee already has a record for the year, use that record
		// otherwise, create a new one
		if (existingAllowance.isPresent()) {
			
			allowance = existingAllowance.get();
		} else {
			
			allowance = new TrainingAllowance();
		}
		// link the allowance record to the employee found
		allowance.setEmployee(employee);
		// store the year passed into setAllowance(..) in this allowance record
		allowance.setYear(year);
		// put the daylimit received by setAllowance(...) into this record
		allowance.setDayLimit(dayLimit);
		// e.g. new BigDecimal("1000.00") would set a yearly fee budget of $1,000.
		allowance.setFeeBudget(feeBudget);
		
		return allowances.save(allowance);
	}
	
	// default allowance when creating a new employee
	public TrainingAllowance createDefaultAllowance(Employee employee) {
		if(employee == null) {
			throw new IllegalArgumentException("Employee is required");
		}
		
		if(employee.getStaffCategory() == null) {
			throw new IllegalArgumentException("Staff category is reuired");
		}
		
		int currentYear = getCurrentYear();
		
		// determine the default annual training day limit based on staff category
		BigDecimal defaultDayLimit; 
		
		// check whether they are administrative staff 
		if (employee.getStaffCategory() == StaffCategory.ADMINISTRATIVE) {
			defaultDayLimit = new BigDecimal("5");
		} else if (employee.getStaffCategory() == StaffCategory.PROFESSIONAL){
			defaultDayLimit = new BigDecimal("10");
		} else {
			throw new IllegalArgumentException("Unsupported staff category");
		}
		
		TrainingAllowance allowance = new TrainingAllowance();
		allowance.setEmployee(employee);
		allowance.setYear(currentYear);
		allowance.setDayLimit(defaultDayLimit);
		allowance.setFeeBudget(new BigDecimal("2000.00"));
		
		return allowances.save(allowance);
		
	}
	
	public void deleteAllowancesForEmployee(Long employeeId) {

	    if (employeeId == null) {
	        throw new IllegalArgumentException("Employee is required");
	    }

	    allowances.deleteByEmployeeId(employeeId);
	}
	
	// get the current year allowance by employeeId
	public TrainingAllowance getCurrentYearAllowance(Long employeeId) {
		if(employeeId == null) {
			throw new IllegalArgumentException("Employee is required");
		}
		
		int currentYear = getCurrentYear();
		
		return allowances.findByEmployeeIdAndYear(employeeId, currentYear).orElse(null);
	}
	
	public int getCurrentYear() {
		return LocalDate.now().getYear();
	}
}
