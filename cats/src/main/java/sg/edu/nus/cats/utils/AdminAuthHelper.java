package sg.edu.nus.cats.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.UserRepository;

@AllArgsConstructor
@Component
public class AdminAuthHelper {
	private final UserRepository users;
	
	public User getAdmin(HttpSession session) {

		Long userId =
				(Long) session.getAttribute("userId");

		if (userId == null) {
			return null;
		}

		User user =
				users.findById(userId).orElse(null);

		if (user == null
				|| !user.isActive()
				|| user.getRole() != Role.ADMIN) {

			return null;
		}

		return user;
	}
}
