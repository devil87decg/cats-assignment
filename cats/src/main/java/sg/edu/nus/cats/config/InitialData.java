package sg.edu.nus.cats.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.AccountService;

@Component
public class InitialData implements CommandLineRunner {

	// A reference lets a variable access an object's methods and data
	// Keep a reference to AccountService so we can call createAccount().
	private final AccountService accounts;
	
	// Keep a reference to UserRepository so we can check whether the admin account already exists.
	private final UserRepository users;

	// Receive the objects supplied by Spring and store their references for use in this class.
	public InitialData(AccountService accounts, UserRepository users) {
		
		this.accounts = accounts;
		this.users = users;
	}
	
	// checks the method's name, parameter types and return type matches the method declared by CommandLineRunner
	@Override
	public void run(String... args) {
		
		// search MySQL for username "admin". If none is found, CATS proceed to create it 
		if (users.findByUsername("admin").isEmpty()) {
			
			accounts.createAccount("admin", "CatsDemo123!", Role.ADMIN);
			
			}
		
		// Temporarily reset the manager account's password for testing.
		User managerAccount = users.findByUsername("manager1").orElse(null);

		if (managerAccount != null) {
		    managerAccount.setPasswordHash(
		            new BCryptPasswordEncoder().encode("ManagerTest123!"));

		    users.save(managerAccount);
		
		}
		
		// Find the test account whose password we want to reset.
		User testAccount = users.findByUsername("employee2").orElse(null);

		// Reset the password only if this account exists.
		if (testAccount != null) {

		    // Convert the new password into a BCrypt hash.
		    testAccount.setPasswordHash(
		            new BCryptPasswordEncoder().encode("EmployeeTest123!"));

		    // Save the new password hash to MySQL.
		    users.save(testAccount);
		}
		
	}
	
}
