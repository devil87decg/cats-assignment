package sg.edu.nus.cats.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.TrainingAllowance;

public interface AllowanceRepository extends JpaRepository<TrainingAllowance, Long> {

	// Find the allowance for this employee in this year
	Optional<TrainingAllowance> findByEmployeeIdAndYear(Long employeeId, int year);
	
	// Delete all allowance records belonging to this employee
    void deleteByEmployeeId(Long employeeId);
}
