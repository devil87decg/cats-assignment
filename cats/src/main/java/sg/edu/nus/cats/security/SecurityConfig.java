package sg.edu.nus.cats.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	// registers the returned object with Spring so other components can use it
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	// method returns the chain that checks incoming requests
	// CatsLoginSuccessHandler connects success handler in SecurityConfig.Java
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			CatsLoginSuccessHandler successHandler)
			
			// allows configuration errors to be reported if building the chain fails 
			throws Exception {
		
		// defines who may access each web address
		http.authorizeHttpRequests(auth -> auth.requestMatchers(
				"/login",
				"/employee/login",
				"/admin/login",
				"/css/**",
				"/js/**",
				"/images/**").permitAll()
				.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
				.requestMatchers("/manager", "/manager/**").hasRole("MANAGER")
				.requestMatchers("/applications", "/applications/**")
				.hasAnyRole("EMPLOYEE", "MANAGER")
				.anyRequest().authenticated());
		
		http.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.failureUrl("/login?error")
				.successHandler(successHandler)
				.permitAll());
		
		http.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.permitAll());
		
		
		return http.build();
	}
	
}
