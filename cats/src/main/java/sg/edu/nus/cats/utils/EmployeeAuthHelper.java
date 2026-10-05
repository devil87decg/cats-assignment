package sg.edu.nus.cats.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import sg.edu.nus.cats.model.Employee;
import sg.edu.nus.cats.model.Role;
import sg.edu.nus.cats.model.User;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.UserRepository;

@Component
@AllArgsConstructor
public class EmployeeAuthHelper {

	// Find the employee profile linked to the logged-in account.
    private final UserRepository users;
    private final EmployeeRepository employees;

    public Employee getEmployee(HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return null;
        }

        User user =
                users.findById(userId).orElse(null);

        if (user == null
                || !user.isActive()
                || (user.getRole() != Role.EMPLOYEE
                    && user.getRole() != Role.MANAGER)) {

            return null;
        }

        Employee employee =
                employees.findByUserId(userId).orElse(null);

        if (employee == null) {
            return null;
        }

        return employee;
    }
}