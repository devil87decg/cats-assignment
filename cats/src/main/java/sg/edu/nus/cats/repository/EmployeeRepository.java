package sg.edu.nus.cats.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.Employee;

// give CATs the standard database operations for Employee records, whose ID type is long
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	// find the employee profile linked to this user ID
	Optional<Employee> findByUserId(Long userId);

	// Find employee profiles that report directly to this manager
	List<Employee> findBySupervisorId(Long supervisorId);

}
