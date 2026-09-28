 INSERT INTO employee (id, name, training_budget, training_days, designation, role, supervisor_id, email, password) VALUES
    (1, 'Tim Cook',0.0, 0.0, 'ceo','manager', 1, 'tim_cook@apple.com', 'password123'),   -- CEO
    (2, 'John Ternus',20000.0, 20.0, 'director','manager', 1, 'john_ternus@apple.com', 'password123'),   
    (3, 'Jony Ive',20000.0, 20.0, 'director','manager', 1, 'jony_ive@apple.com', 'password123'),   
    (4, 'Sabih Khan', 20000.0, 20.0, 'manager','manager', 2, 'sabih_khan@apple.com', 'password123'),
    (5, 'Kevan Parekh', 20000.0, 20.0, 'manager','manager', 2, 'kevan_parekh@apple.com', 'password123'),
    (6, 'Eddy Cue', 10000.0, 10.0, 'executive','staff', 4, 'eddy_cue@apple.com', 'password123'),
    (7, 'Craig Federighi', 10000.0, 10.0, 'executive','staff', 4, 'craig@apple.com', 'password123'),
    (8, 'Greg Joz', 10000.0, 10.0, 'executive','staff', 5, 'greg@apple.com', 'password123'),
    (9, 'Jennifer Newstead', 10000.0, 10.0, 'executive','staff', 5, 'jennifer@apple.com', 'password123');   
    
 INSERT INTO 
 	course (id, course_name, training_provider, start_date, end_date, fees, type)
 VALUES
 	(1, 'Fundamentals of Programming in Java', 'nus-iss', '2027-12-01', '2027-12-03', 2000.0, 'external'),
 	(2, 'Object Oriented Programming in Java', 'nus-iss', '2027-11-01', '2027-11-03', 2000.0, 'external'),
 	(3, 'Dining Etiquette', 'Laselle', '2027-11-15', '2027-11-17', 2000.0, 'professional'),
 	(4, 'Basic Email Report Writing', 'internal','2027-10-03', '2027-10-04', 0.0, 'internal'),
 	(5, 'Building Web Application using Spring Boot', 'nus-iss', '2027-09-05', '2027-09-09', 2000.0, 'external');
 	
INSERT INTO 
    course_application (id, application_date, decision_date, last_updated_at, emp_justification, work_dissemination, mgr_reason, experience_comments, status, course_id, employee_id, decided_by)
VALUES
    (1, '2027-09-01', '2027-09-05', '2027-09-05', 'I would like to strengthen my Java programming fundamentals for my current development work.', 'I will conduct a knowledge sharing session with the development team after completing the course.', 'The course is relevant to the employee''s current responsibilities and development goals.', NULL, 'APPROVED', 1, 6, 4),
    (2, '2027-09-10', NULL, '2027-09-10', 'I want to improve my object-oriented programming skills and apply better design practices in our projects.', 'I will document the key OOP concepts and share examples with the team.', NULL, NULL, 'APPLIED', 2, 7, NULL),
    (3, '2027-09-12', '2027-09-15', '2027-09-15', 'The course will improve my professional communication and confidence when attending external business events.', 'I will share the key etiquette guidelines with colleagues.', 'The course is not sufficiently relevant to the employee''s current job responsibilities.', NULL, 'REJECTED', 3, 8, 5),
    (4, '2027-09-15', '2027-09-17', '2027-09-17', 'I would like to improve the clarity and professionalism of my written communication.', 'I will prepare a short writing guide containing the key lessons from the course.', 'The course will improve communication skills required for the employee''s role.', NULL, 'APPROVED', 4, 9, 5),
    (5, '2027-08-20', NULL, '2027-09-01', 'I would like to develop stronger Spring Boot skills to support upcoming web application projects.', 'I will build a sample Spring Boot application and conduct a demonstration for the team.', NULL, NULL, 'UPDATED', 5, 9, NULL),
    (6, '2027-06-01', '2027-06-05', '2027-09-10', 'The course will help me develop production-ready web applications using Spring Boot.', 'I will share reusable Spring Boot examples and coding practices with the team.', 'Approved as the course directly supports upcoming application development work.', 'The course was useful and provided practical experience in building Spring Boot applications.', 'COMPLETED', 5, 6, 4),
    (7, '2027-08-01', NULL, '2027-08-05', 'I would like to improve my professional etiquette when interacting with external stakeholders.', 'I will share useful professional etiquette practices with my colleagues.', NULL, NULL, 'CANCELLED', 3, 7, NULL),
    (8, '2027-09-20', NULL, '2027-09-20', 'Java is required for several upcoming projects and I would like to strengthen my programming foundation.', 'I will prepare sample Java exercises for colleagues who are interested in learning Java.', NULL, NULL, 'APPLIED', 1, 8, NULL),
    (9, '2027-07-01', '2027-07-03', '2027-10-05', 'I want to improve the quality of my emails and reports.', 'I will share a checklist for writing clear and concise business emails.', 'Approved because effective written communication is important for the employee''s responsibilities.', 'The course was helpful and I have started applying the techniques to my weekly reports.', 'COMPLETED', 4, 9, 5),
    (10, '2027-08-15', '2027-08-18', '2027-08-18', 'I would like to improve my software design and Java programming skills.', 'I will share examples of object-oriented design patterns with the development team.', 'The employee should complete the Java fundamentals course before attending this course.', NULL, 'REJECTED', 2, 2, 1);