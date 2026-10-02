package sg.edu.nus.cats.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.ApplicationStatus;
import sg.edu.nus.cats.model.CourseApplication;

// Tells Spring Data JPA: This repository handles CourseApplication records, and their IDs are long.
public interface ApplicationRepository extends JpaRepository<CourseApplication, Long>{

	// Find applications belonging to this employee whose start date falls between these two dates.
	// List<CourseApplication> as it may return several applications, or an empty list if none match.
	// For e.g., to find employee's application in 2025, we give the method;
	// first = 2025-01-01
	// last = 2025-12-31
	List<CourseApplication> findByEmployeeIdAndStartDateBetween(
			Long employeeId, LocalDate first, LocalDate last);
	
	// EmployeeSupervisorId -> find applications where the applicant's supervisor has this ID.
	// StatusIn -> include only applications whose status is in the set provided
	// For e.g. passing APPLIED asks for applications that;
	// Belong to employees supervised by that manager, and have APPLIED status
	List<CourseApplication> findByEmployeeSupervisorIdAndStatusInOrderByEmployeeIdAsc(
			Long managerId, Collection<ApplicationStatus> statues);
	
	// All applicantion for this employee, including ones starting in another year
	List<CourseApplication> findByEmployeeId(Long employeeId);
	
	// Find this manager's subordinates applications with its status
	List<CourseApplication> findByEmployeeSupervisorIdAndStatus(Long managerId, ApplicationStatus status);
	
}
