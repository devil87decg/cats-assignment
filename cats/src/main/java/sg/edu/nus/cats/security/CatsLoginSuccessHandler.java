package sg.edu.nus.cats.security;

import java.io.IOException;
import java.time.Year;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;

@Component
public class CatsLoginSuccessHandler implements AuthenticationSuccessHandler {

	
	private final UserRepository users;
	
	public CatsLoginSuccessHandler(UserRepository users) {
		
		this.users = users;
	}

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication)
			throws IOException, ServletException {
		
		// get the username that Spring Security has already authenticated
		String username = authentication.getName();
		
		User user = users.findByUsername(username)
				.orElseThrow(() -> new ServletException("Authenticated account not found"));
		
		HttpSession session = request.getSession();
		
		session.setAttribute("userId", user.getId());
		session.setAttribute("loggedInName", user.getUsername());
		session.setAttribute("role", user.getRole().name());
		session.setAttribute("year", Year.now().getValue());	
		
		if (user.getRole() == Role.ADMIN) {
			
			// tells browser to open the admin page
			response.sendRedirect(request.getContextPath() + "/admin");
		
		// handles employees and managers
		} else {
			
			// sends them to home page
			response.sendRedirect(request.getContextPath() + "/");
		}
		
	}
	
}
