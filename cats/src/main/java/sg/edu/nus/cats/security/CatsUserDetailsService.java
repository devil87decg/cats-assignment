package sg.edu.nus.cats.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;

@Service
public class CatsUserDetailsService implements UserDetailsService {

	private final UserRepository users;
	
	public CatsUserDetailsService(UserRepository users) {
		
		this.users = users;
	}
	
	@Override
	// loadUserByUsername(String username) -> receives the username entered during login
	public UserDetails loadUserByUsername(String username)
			
			// declare that the lookup may fail because the account doesn't exist
			throws UsernameNotFoundException {
		
		User user = users.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found")); 
		
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPasswordHash())
				.roles(user.getRole().name())
				.disabled(!user.isActive())
				.build();
		
	}
	
}
