package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.StaffCategory;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.ApplicationRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.UserRepository;

@Service
public class EmployeeService {

	// Keep a reference to the repository used to find and save employee profiles.
	private final EmployeeRepository employees;
	
	// Keep a reference to the repository used to find the employee's login account.
	private final UserRepository users;
	
	private final AccountService accounts;
	private final ApplicationRepository applications;
	
	private final AllowanceService allowanceService;
	
	// Receive the repositories from Spring and store their references.
	public EmployeeService(EmployeeRepository employees, UserRepository users, AccountService accounts,
			ApplicationRepository applications, AllowanceService allowanceService) {
		
		this.employees = employees;
		this.users = users;
		this.accounts = accounts;
		this.applications = applications;
		this.allowanceService = allowanceService;
	}
	
	// paramters supply the profile details required 
	public Employee createProfile(Long userId, String name, String email, String designation, String department,
			StaffCategory staffCategory, Long supervisorId) {
		
		// check that a login account ID was supplied
		if (userId == null) {
			
			throw new IllegalArgumentException("Login account is required");
		}
		
		if (name == null || name.isBlank()) {
			
			throw new IllegalArgumentException("Employee name is required");
			
		}
		
		if (email == null || email.isBlank()) {
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
		employee.setEmail(email);
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
	
	// Find all employee profiles
	public List<Employee> findAllEmployees() {
		return employees.findAll();
	}
	
	// Find an employee profile by employee ID
	public Employee findEmployeeById(Long employeeId) {
		return employees.findById(employeeId)
				.orElseThrow(() -> 
					new IllegalArgumentException("Employee not found"));
	}
	
	// Update an existing employee profile
	@Transactional
	public Employee updateEmployee(
			Long employeeId,
			String name,
			String email,
			Role role,
			String designation,
			String department,
			StaffCategory staffCategory,
			Long supervisorId) {
		
		// Find the existing employee
		Employee employee = employees.findById(employeeId)
				.orElseThrow(() ->
					new IllegalArgumentException("Employee not found"));
		
		// Validate required fields
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Employee name is required");
		}
		
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Employee email is required");
		}
		if (role == null) {
			throw new IllegalArgumentException("Role is required");
		}
		
		if (staffCategory == null) {
			throw new IllegalArgumentException("Staff category is required");
		}
		
		User user = employee.getUser();

		if (user.getRole() == Role.MANAGER
				&& role != Role.MANAGER) {
			
			List<Employee> subordinates =
					employees.findBySupervisorId(employeeId);
			
			if (!subordinates.isEmpty()) {
				throw new IllegalArgumentException(
						"Cannot change manager role because this employee has subordinates");
			}
		}

		user.setRole(role);
		users.save(user);
		
		// Update the existing employee object
		employee.setName(name);
		employee.setEmail(email);
		employee.setDesignation(designation);
		employee.setDepartment(department);
		employee.setStaffCategory(staffCategory);
		
		// Remove supervisor if none was selected
		if (supervisorId == null) {
			
			employee.setSupervisor(null);
			
		} else {
			
			// An employee cannot supervise themselves
			if (employeeId.equals(supervisorId)) {
				throw new IllegalArgumentException(
						"Employee cannot be their own supervisor");
			}
			
			Employee supervisor = employees.findById(supervisorId)
					.orElseThrow(() ->
						new IllegalArgumentException("Supervisor not found"));
			
			// Supervisor must have the MANAGER role
			if (supervisor.getUser().getRole() != Role.MANAGER) {
				throw new IllegalArgumentException(
						"Supervisor must have the manager role");
			}
			
			employee.setSupervisor(supervisor);
		}
		
		// Save the changes
		return employees.save(employee);
	}
	
	// Find employees whose login account has the MANAGER role
	public List<Employee> findManagers() {
		return employees.findByUserRole(Role.MANAGER);
	}
	
	// Delete an existing employee profile together with its login account
	@Transactional
	public boolean deleteEmployee(Long employeeId) {
		
		// Find the employee first
		Employee employee = employees.findById(employeeId)
				.orElseThrow(() ->
					new IllegalArgumentException("Employee not found"));
		
		// Check whether other employees report to this employee
		List<Employee> subordinates =
				employees.findBySupervisorId(employeeId);
		
		if (!subordinates.isEmpty()) {
			throw new IllegalArgumentException(
					"Cannot remove employee because they currently have subordinates. "
							+ "Reassign the subordinates first.");
		}
		
		// Keep the linked user so it can be deleted after the employee
		User user = employee.getUser();
		
		// Preserve employees that form part of application history.
		boolean hasApplications =
				applications.existsByEmployeeId(employeeId);

		boolean hasDecisions =
				applications.existsByDecidedById(employeeId);

		if (hasApplications || hasDecisions) {

			user.setActive(false);
			users.save(user);

			return false;
		}
		
		// No historical records exist, so the employee can be fully deleted.

		// Delete the employee's training allowance first because
		// training_allowance has a foreign key referencing employees.
		allowanceService.deleteAllowancesForEmployee(employeeId);

		// Now it is safe to delete the employee and user account.
		employees.delete(employee);
		users.delete(user);
		
		return true;
	}
	
	@Transactional
	public Employee createEmployeeWithAccount(
			String username,
			String password,
			Role role,
			String name,
			String email,
			String designation,
			String department,
			StaffCategory staffCategory,
			Long supervisorId) {

		// Create the login account first.
		User user = accounts.createAccount(
				username,
				password,
				role);

		// Create the employee profile linked to that account.
		Employee employee = createProfile(user.getId(), name, email, designation, department, staffCategory,
				supervisorId);
		
		// Set the default allowance for the new employee
		allowanceService.createDefaultAllowance(employee);
		
		return employee;
	}
}
