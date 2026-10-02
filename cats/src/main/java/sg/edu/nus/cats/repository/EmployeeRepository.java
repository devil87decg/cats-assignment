package sg.edu.nus.cats.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;

// give CATs the standard database operations for Employee records, whose ID type is long
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	// find the employee profile linked to this user ID
	Optional<Employee> findByUserId(Long userId); //Actually this one like no need because it is derived.

	// Find employee profiles that report directly to this manager
	List<Employee> findBySupervisorId(Long supervisorId);
	
	// Find employee profiles whose login account has the specified role
	List<Employee> findByUserRole(Role role);
}
