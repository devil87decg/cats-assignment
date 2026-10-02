package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.StaffCategory;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.UserRepository;

@Service
public class EmployeeService {

	// Keep a reference to the repository used to find and save employee profiles.
	private final EmployeeRepository employees;
	
	// Keep a reference to the repository used to find the employee's login account.
	private final UserRepository users;
	
	// Receive the repositories from Spring and store their references.
	public EmployeeService(EmployeeRepository employees, UserRepository users) {
		
		this.employees = employees;
		this.users = users;
		
	}
	
	// paramters supply the profile details required 
	public Employee createProfile(Long userId, String name, String designation, String department,
			StaffCategory staffCategory, Long supervisorId) {
		
		// check that a login account ID was supplied
		if (userId == null) {
			
			throw new IllegalArgumentException("Login account is required");
		}
		
		if (name == null || name.isBlank()) {
			
			throw new IllegalArgumentException("Employee name is required");
			
		}
		
		if (staffCategory == null) {
			
			throw new IllegalArgumentException("Staff category is required");
		}
		
		// searches for the supplied ID, if found, user refers to that account
		User user = users.findById(userId).orElseThrow(() -> new IllegalArgumentException
				("Login account not found"));
		
		// if a profile already exists for this login account, stop creating another one
		if (employees.findByUserId(userId).isPresent()) {
			
			throw new IllegalArgumentException("This account already has an employee profile");
		}
		
		// calls the no-arg constructor in Employee.Java and creates a new profile in memory
		Employee employee = new Employee();
		
		// link the new employee profile to the login account we found 
		employee.setUser(user);
		// if supplied name to createProfile(..) is "Alice Tan", the profile now holds that name
		employee.setName(name);
		employee.setDesignation(designation);
		employee.setDepartment(department);
		employee.setStaffCategory(staffCategory);
		
		// if supervisorid was supplied, find that employee and link them as the new profile's supervisor
		// if the id does not exist, creation of account stops with an error
		if (supervisorId != null) {
			
			Employee supervisor = employees.findById(supervisorId).
					orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));
			
			// get the supervisor's linked login account and check its role
			if (supervisor.getUser().getRole() != Role.MANAGER) {
				
				throw new IllegalArgumentException("Supervisor must have the manager role");
			}
			
			employee.setSupervisor(supervisor);
		}
		
		return employees.save(employee); 
	}
	
	// Find the employees who report directly to this manager
	public List<Employee> findSubordinates(Employee manager) {
		
		// ask EmployeeRepository for profiles linked to this supervisor
		return employees.findBySupervisorId(manager.getId());
	}
	
	public Employee findSubordinate(Long employeeId, Employee manager) {
		
		// find the employee selected through the history link
		// example clicking employee's history link -> /manager/subordinates/3/history -> employeeService.findSubordinate(3L, manager)
		Employee subordinate = employees.findById(employeeId).
				orElseThrow(() -> new IllegalArgumentException("Employee not found"));
		
		Employee supervisor = subordinate.getSupervisor();
		
		// reject if no supervisor assigned or supervisor is a different manager
		if (supervisor == null || !supervisor.getId().equals(manager.getId())) {
			
			throw new IllegalArgumentException("You can only view your own subordinates' course histroy");
		}
		
		return subordinate;
		
	}
	
	
}
