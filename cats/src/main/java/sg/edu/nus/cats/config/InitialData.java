package sg.edu.nus.cats.config;

import org.springframework.boot.CommandLineRunner;

import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.repository.UserRepository;
import sg.edu.nus.cats.service.AccountService;

//@Component
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
		
	}
	
}
