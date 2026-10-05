package sg.edu.nus.cats.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import sg.edu.nus.cats.model.ApplicationStatus;

@Service
public class EmailService {

	private final JavaMailSender mailSender;
	private final String from;
	private final String baseUrl;
	private final String loginPath;

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	public EmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String from,
			@Value("${app.base-url}") String baseUrl, @Value("${app.login-path}") String loginPath) {
		super();
		this.mailSender = mailSender;
		this.from = from;
		this.baseUrl = baseUrl;
		this.loginPath = loginPath;
	}

	private String loginUrl() {
		return baseUrl + loginPath;
	}

	private void send(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);

		try {
			mailSender.send(message);
			logger.info("Email successfully sent to {}", to);
		} catch (MailException e) {
			logger.error("Email could not be sent to {}. Application will continue. Reason: {}", to, e.getMessage());
		}
	}

	public void notifyManagerOfSubmission(String managerEmail, String managerName, String employeeName) {

		String subject = "Course application submitted-" + employeeName;
		String body = "Hello " + managerName + ",\n\n" + employeeName + " has submitted a course application.\n"
				+ "Please log in to review the application and comments.\n\n" + "Login: " + loginUrl() + "\n\n"
				+ "Sent By,\nCourse Application System (CATS)";

		send(managerEmail, subject, body);
	}

	public void notifyEmployeeOfDecision(ApplicationStatus applicationStatus, String managerReason, String employeeName,
			String employeeEmail) {
		String subject = "Course application" + applicationStatus;
		String reason = managerReason == null ? "No reason provided." : managerReason;
		String body = "Hello " + employeeName + ",\n\n" + "Your course application has been " + applicationStatus
				+ ".\n\n" + "Manager's reason: " + reason + "\n\n" + loginUrl() + "\n\n"
				+ "Sent By,\nCourse Application System (CATS)";

		send(employeeEmail, subject, body);
	}

}
