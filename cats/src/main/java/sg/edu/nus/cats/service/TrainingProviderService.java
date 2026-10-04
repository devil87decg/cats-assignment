package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.TrainingProvider;
import sg.edu.nus.cats.repository.TrainingProviderRepository;

@AllArgsConstructor
@Service
public class TrainingProviderService {
	private final TrainingProviderRepository providers;
	
	public List<TrainingProvider> findAll() {
		return providers.findAll();
	}


	public List<TrainingProvider> findActive() {
		return providers.findByActiveTrue();
	}


	public TrainingProvider findById(Long id) {

		return providers.findById(id)
				.orElseThrow(() ->
					new IllegalArgumentException(
						"Training provider not found"));
	}


	public TrainingProvider create(
			String name,
			String description) {

		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException(
					"Training provider name is required");
		}

		String cleanName = name.trim();

		if (providers.findByName(cleanName).isPresent()) {
			throw new IllegalArgumentException(
					"Training provider already exists");
		}

		TrainingProvider provider =
				new TrainingProvider();

		provider.setName(cleanName);
		provider.setDescription(description);
		provider.setActive(true);

		return providers.save(provider);
	}


	public TrainingProvider update(
			Long id,
			String name,
			String description) {

		TrainingProvider provider = findById(id);

		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException(
					"Training provider name is required");
		}

		String cleanName = name.trim();

		providers.findByName(cleanName)
				.ifPresent(existing -> {

					if (!existing.getId().equals(id)) {
						throw new IllegalArgumentException(
								"Training provider already exists");
					}
				});

		provider.setName(cleanName);
		provider.setDescription(description);

		return providers.save(provider);
	}


	public void deactivate(Long id) {

		TrainingProvider provider = findById(id);

		provider.setActive(false);

		providers.save(provider);
	}


	public void reactivate(Long id) {

		TrainingProvider provider = findById(id);

		provider.setActive(true);

		providers.save(provider);
	}
}
