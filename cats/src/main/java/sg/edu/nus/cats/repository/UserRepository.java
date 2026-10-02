package sg.edu.nus.cats.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.model.User;

// UserRepository is how CATs asks MySQL about records in the users table and save changes to them 
// Spring Data JPA repository: gives your code methods such as save(), findById()
// So basically telling Spring -> make me a repository that works with User rows, whose IDs are Long
public interface UserRepository extends JpaRepository<User, Long> {

	// Find a user whose username matches the value provided.
	// Optional<User> tells Java that the method will return with : (1) the result might contain one user, or it might not contain no user.
	Optional<User> findByUsername(String username);
	
	
	
}
