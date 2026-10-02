package sg.edu.nus.cats.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.model.Role;

@Service
public class AccountService {

	private final UserRepository users;
	
	// create a BCrypt password encoder and keep it in this service under passwordEncoder
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public AccountService(UserRepository users) {
		
		this.users = users;
	}	
		// create an account with this username, password and role
		public User createAccount(String username, String password, Role role) {
			
			// check a username is entered 
			if (username == null || username.isBlank()) {
				
				throw new IllegalArgumentException("Username is required");
			}
			
			if (password == null || password.isBlank()) {
				
				throw new IllegalArgumentException("Password is required");
			}
			
			if (role == null) {
				
				throw new IllegalArgumentException("Role is required");
			}
			
			// serach for this username, if it already exists, stop account creation
			if (users.findByUsername(username).isPresent()) {
				
				
				throw new IllegalArgumentException("Username is already taken");
			}
			
			User user = new User();
			
			// put the username supplied to createAccount(...) into new user object
			user.setUsername(username);
			
			user.setPasswordHash(passwordEncoder.encode(password));
			
			user.setRole(role);
			
			// saves the new User record in MySQL
			return users.save(user); 
		}
}
