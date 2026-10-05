package sg.edu.nus.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import sg.edu.nus.cats.model.TrainingProvider;

public interface TrainingProviderRepository extends JpaRepository<TrainingProvider, Long> {
	Optional<TrainingProvider> findByName(String name);

	List<TrainingProvider> findByActiveTrue();
}
