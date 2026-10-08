package sg.edu.nus.cats.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.ClaimStatus;
import sg.edu.nus.cats.model.CourseFeeClaim;

public interface ClaimRepository extends JpaRepository<CourseFeeClaim, Long> {

	// Check whether a reimbursement claim already exists for this application.
	boolean existsByApplicationId(Long applicationId);

	// Find the claim belonging to a particular course application.
	Optional<CourseFeeClaim> findByApplicationId(Long applicationId);

	// Find all reimbursement claims belonging to an employee.
	List<CourseFeeClaim> findByApplicationEmployeeId(Long employeeId);

	// Find an employee's claims with a particular claim status.
	List<CourseFeeClaim> findByApplicationEmployeeIdAndStatus(
			Long employeeId, ClaimStatus status);
	
	// Find an employee's reimbursement claims for courses starting
	// within the supplied date range.
	List<CourseFeeClaim> findByApplicationEmployeeIdAndApplicationStartDateBetween(
			Long employeeId,
			LocalDate firstDay,
			LocalDate lastDay);
	
	// Find claims with a particular status belonging to
	// employees supervised by this manager.
	List<CourseFeeClaim>
	findByApplicationEmployeeSupervisorIdAndStatusOrderByClaimDateAsc(
			Long managerId,
			ClaimStatus status);
	
	// All claim history for an employee.
	List<CourseFeeClaim>
	    findByApplicationEmployeeIdOrderByClaimDateDesc(
	        Long employeeId);

	// Claims for courses belonging to a selected year.
	List<CourseFeeClaim>
	    findByApplicationEmployeeIdAndApplicationStartDateBetweenOrderByClaimDateDesc(
	        Long employeeId,
	        LocalDate firstDay,
	        LocalDate lastDay);
}