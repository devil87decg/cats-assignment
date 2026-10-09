SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- USERS
-- Preserve the original demo users, IDs, roles and password hashes.
-- ============================================================
INSERT IGNORE INTO users (id, username, active, password_hash, role) VALUES
(1, 'admin',      b'1', '$2a$10$ujWik98HXV4LRhHycVP/QOSG2x9K.M5EElZkuShmWmyYhP18NMcM.', 'ADMIN'),
(2, 'xiang xuan', b'1', '$2a$10$HSjlB9suCskul1SVJ2l2C.ATwSzgp1bL3tBUof.jd5XSOdmgu5NPC', 'EMPLOYEE'),
(3, 'manager1',   b'1', '$2a$10$4jL0fqmHITYe2ixzdR2p/eLvHP1i6ee1wyDsAetftch/p/Pb5VtEy', 'MANAGER'),
(4, 'employee2',  b'1', '$2a$10$k585OedWPUdUdV/3lcHrCeqzjs3rnsbrsZUbk3pJ63TRCRie.6Zy6', 'EMPLOYEE'),
(5, 'manager2',   b'1', '$2a$10$UaQQjacpa3TmoL29Crfxfe8117Rm6Ouw3Lk9W9LWSnCsnU1Y1YT0u', 'MANAGER');

-- ============================================================
-- EXTENDED DEMO USERS
-- Password for each account is the same as its username.
-- Pattern per person: base=ADMIN, base2=EMPLOYEE, base3=MANAGER.
-- IDs start at 101 to avoid colliding with the original demo data.
-- ============================================================
INSERT IGNORE INTO users (id, username, active, password_hash, role) VALUES
(101, 'syahmul',   b'1', '$2a$10$lT.jMNqBlOGdQghSi7t9vuzqC1txQi1Ow.CFmb/RPsXrnkFPne2vK', 'ADMIN'),
(102, 'syahmul2',  b'1', '$2a$10$eFFcHUw6QnBn8X2Mo4Fp/OVpzD5SNggbzS.jQro4L8a3/TVAheami', 'EMPLOYEE'),
(103, 'syahmul3',  b'1', '$2a$10$jqkztC.vwTzng5Rw9JJRTOKB1Lqr42Yv/CJkOkV1gsXBRsF8E9CLi', 'MANAGER'),
(104, 'hafizah',   b'1', '$2a$10$zMJVn1bN9SKbEZcR8yj19eLGEeRkHU/E53k24WhKF8skZRj8P8vSK', 'ADMIN'),
(105, 'hafizah2',  b'1', '$2a$10$56yPJmMPRo9PBNTNEY8PYe8rdaBj2kMLPMF1rwM5KPuZWs4MC0a7m', 'EMPLOYEE'),
(106, 'hafizah3',  b'1', '$2a$10$lKoW5nNiiv8ZrQzH4ndl..xPMzl597X8QubzPc02Sl4s/sgveV0lK', 'MANAGER'),
(107, 'chloe',     b'1', '$2a$10$0i/nYMTnjG4ECUhcHwenrOlfHerD8TU5gipTPvd92hj48SEHDPcDO', 'ADMIN'),
(108, 'chloe2',    b'1', '$2a$10$ltf2MzAY65FuFKOozdpq.Oo0oK/JiO0lWI/kHzw38xDZamUMUkbxW', 'EMPLOYEE'),
(109, 'chloe3',    b'1', '$2a$10$wC0Bq2KtsdBNyMiPBkZj9eaSR1RjGWuAghWur.iAxQPAK/4XhqiQC', 'MANAGER'),
(110, 'aufa',      b'1', '$2a$10$3ljlAwKtbNfcD6z8Z/2EPu9wncHgYUNukrdBypqgdTrM1FI9jQC4C', 'ADMIN'),
(111, 'aufa2',     b'1', '$2a$10$f/9AhTiMsWf4uKtIwrgGxevUHIxLq7qJ.rFgtnRWYUa5Zxq2ZtOvi', 'EMPLOYEE'),
(112, 'aufa3',     b'1', '$2a$10$Qg5/WBUSIeNCf8fcVuhR2eg.uyKVWhgLFF.aWCum.AiesFW8wPUDS', 'MANAGER'),
(113, 'xiuming',   b'1', '$2a$10$el.wDnBs0K.116LR7VyVDekVuUsE38CwgC2UFjNJAbh.BS5Kg6/5q', 'ADMIN'),
(114, 'xiuming2',  b'1', '$2a$10$YZEEpTljoKyC0Uh09r5bHuyYJNpLBv2j7c2biC99HUGRJhcyPJ.Ia', 'EMPLOYEE'),
(115, 'xiuming3',  b'1', '$2a$10$GPrL2jtz9YBIoeT5XYmg1OfFMwqgrCjaeFCO3Jz1IcBTJ8.i0ZDyW', 'MANAGER'),
(116, 'xinxian',   b'1', '$2a$10$yigMdGHfwCUEnW1ROQLm9u/MTehUqDOJo12PFFaEEA0EFTTUUtQOC', 'ADMIN'),
(117, 'xinxian2',  b'1', '$2a$10$BUuZpnyB2IrXPkNMxCLFrO3nIPG1XiNZyBnBxiqZi1zOtypd8LyKe', 'EMPLOYEE'),
(118, 'xinxian3',  b'1', '$2a$10$fN5Pi5MO2PrhjCsx0yfZeOEj3.ykC/qvOJ0dYVqbjc2Sqskk1AOxG', 'MANAGER'),
(119, 'ramesh',    b'1', '$2a$10$4huxYbB2pBmqo0YYvaY1MexGe7IiwcudCP9LrDRBaUVEgtmIBaPHu', 'ADMIN'),
(120, 'ramesh2',   b'1', '$2a$10$0QhFEJLgnxXSXux9ftJmg.zm/kvuW81OCRnjztCtE.hkACVhxUyFO', 'EMPLOYEE'),
(121, 'ramesh3',   b'1', '$2a$10$mkKUiBSmGW3lFcSh0MBOCOltZdPPLw.l9fgLfAbAFKYrJNA80Jtem', 'MANAGER'),
(122, 'dominic',   b'1', '$2a$10$sPov33e2aIrDaIYObOacue06zzFphY4DueqAE7YiRLfKtfQzMZjL2', 'ADMIN'),
(123, 'dominic2',  b'1', '$2a$10$n2WaUnTLiXE1Y43sC9BH6e.LoOu.5/gQvvqW4WVfYLbRFvYt47RHy', 'EMPLOYEE'),
(124, 'dominic3',  b'1', '$2a$10$aYrIhsTPohwl9CLnVKC0je01TxgPFfJg.yo1C4giZ9wcGTSKELkQK', 'MANAGER'),
(125, 'ceo',       b'1', '$2a$12$DasSwOUzsJFfdVhZhRfJXevplHt2w0bNYtKq2cB/YwUJcZd3f/njq', 'MANAGER');

-- ============================================================
-- EMPLOYEES
-- Preserve the original demo employees and reporting structure.
-- Email addresses are demo values used for application notifications.
-- ============================================================
INSERT IGNORE INTO employees
(id, department, designation, name, staff_category, supervisor_id, user_id, email)
VALUES
(2, 'IT ',         'Manager',          'Dogbert',    'PROFESSIONAL', NULL, 3, 'xin.xian.quek@u.nus.edu'),
(1, '',            '',                 'xiang xuan', 'PROFESSIONAL', 2,    2, 'employee1@example.com'),
(3, 'Admin Staff', 'Secretary',        'Ratbert',    'PROFESSIONAL', 2,    4, 'employee2@example.com'),
(4, 'Finance',     'Accounts Manager', 'Kuan Yew',   'PROFESSIONAL', NULL, 5, 'manager2@example.com');

-- ============================================================
-- EXTENDED DEMO EMPLOYEES
-- Insert all employee profiles first without supervisor links.
-- ============================================================

INSERT IGNORE INTO employees
(id, department, designation, name, staff_category,
 supervisor_id, user_id, email)
VALUES
(101, 'Administration', 'System Administrator',
 'Syahmul Admin', 'ADMINISTRATIVE',
 NULL, 101, 'syahmul@example.com'),

(102, 'Technology', 'Software Engineer',
 'Syahmul Employee', 'PROFESSIONAL',
 NULL, 102, 'syahmul2@example.com'),

(103, 'Technology', 'Engineering Manager',
 'Syahmul Manager', 'PROFESSIONAL',
 NULL, 103, 'syahmul3@example.com'),

(104, 'Administration', 'HR Administrator',
 'Hafizah Admin', 'ADMINISTRATIVE',
 NULL, 104, 'hafizah@example.com'),

(105, 'Human Resources', 'HR Executive',
 'Hafizah Employee', 'PROFESSIONAL',
 NULL, 105, 'hafizah2@example.com'),

(106, 'Human Resources', 'HR Manager',
 'Hafizah Manager', 'PROFESSIONAL',
 NULL, 106, 'hafizah3@example.com'),

(107, 'Administration', 'Operations Administrator',
 'Chloe Admin', 'ADMINISTRATIVE',
 NULL, 107, 'chloe@example.com'),

(108, 'Operations', 'Operations Analyst',
 'Chloe Employee', 'PROFESSIONAL',
 NULL, 108, 'chloe2@example.com'),

(109, 'Operations', 'Operations Manager',
 'Chloe Manager', 'PROFESSIONAL',
 NULL, 109, 'chloe3@example.com'),

(110, 'Administration', 'Programme Administrator',
 'Aufa Admin', 'ADMINISTRATIVE',
 NULL, 110, 'aufa@example.com'),

(111, 'Programmes', 'Programme Executive',
 'Aufa Employee', 'PROFESSIONAL',
 NULL, 111, 'aufa2@example.com'),

(112, 'Programmes', 'Programme Manager',
 'Aufa Manager', 'PROFESSIONAL',
 NULL, 112, 'aufa3@example.com'),

(113, 'Administration', 'Finance Administrator',
 'Xiuming Admin', 'ADMINISTRATIVE',
 NULL, 113, 'xiuming@example.com'),

(114, 'Finance', 'Finance Analyst',
 'Xiuming Employee', 'PROFESSIONAL',
 NULL, 114, 'xiuming2@example.com'),

(115, 'Finance', 'Finance Manager',
 'Xiuming Manager', 'PROFESSIONAL',
 NULL, 115, 'xiuming3@example.com'),

(116, 'Administration', 'IT Administrator',
 'Xinxian Admin', 'ADMINISTRATIVE',
 NULL, 116, 'xinxian@example.com'),

(117, 'IT', 'Systems Analyst',
 'Xinxian Employee', 'PROFESSIONAL',
 NULL, 117, 'xinxian2@example.com'),

(118, 'IT', 'IT Manager',
 'Xinxian Manager', 'PROFESSIONAL',
 NULL, 118, 'xinxian3@example.com'),

(119, 'Administration', 'Learning Administrator',
 'Ramesh Admin', 'ADMINISTRATIVE',
 NULL, 119, 'ramesh@example.com'),

(120, 'Learning', 'Learning Specialist',
 'Ramesh Employee', 'PROFESSIONAL',
 NULL, 120, 'ramesh2@example.com'),

(121, 'Learning', 'Learning Manager',
 'Ramesh Manager', 'PROFESSIONAL',
 NULL, 121, 'ramesh3@example.com'),

(122, 'Administration', 'Business Administrator',
 'Dominic Admin', 'ADMINISTRATIVE',
 NULL, 122, 'dominic@example.com'),

(123, 'Business', 'Business Analyst',
 'Dominic Employee', 'PROFESSIONAL',
 NULL, 123, 'dominic2@example.com'),

(124, 'Business', 'Business Manager',
 'Dominic Manager', 'PROFESSIONAL',
 NULL, 124, 'dominic3@example.com'),
 
 (125, 'CEO', 'CEO', 
 'Tim CEO', 'PROFESSIONAL', 
 NULL, 125, 'CEO@example.com');
 
 -- ============================================================
-- EXTENDED DEMO REPORTING RELATIONSHIPS
-- All referenced manager employee records now exist.
-- ============================================================

UPDATE employees SET supervisor_id = 103 WHERE id = 102;
UPDATE employees SET supervisor_id = 106 WHERE id = 105;
UPDATE employees SET supervisor_id = 109 WHERE id = 108;
UPDATE employees SET supervisor_id = 112 WHERE id = 111;
UPDATE employees SET supervisor_id = 115 WHERE id = 114;
UPDATE employees SET supervisor_id = 118 WHERE id = 117;
UPDATE employees SET supervisor_id = 121 WHERE id = 120;
UPDATE employees SET supervisor_id = 124 WHERE id = 123;
UPDATE employees SET supervisor_id = 125 WHERE id = 103;
UPDATE employees SET supervisor_id = 125 WHERE id = 106;
UPDATE employees SET supervisor_id = 125 WHERE id = 109;
UPDATE employees SET supervisor_id = 125 WHERE id = 112;
UPDATE employees SET supervisor_id = 125 WHERE id = 115;
UPDATE employees SET supervisor_id = 125 WHERE id = 118;
UPDATE employees SET supervisor_id = 125 WHERE id = 121;
UPDATE employees SET supervisor_id = 125 WHERE id = 124;
UPDATE employees SET supervisor_id = 125 WHERE id = 2;
UPDATE employees SET supervisor_id = 125 WHERE id = 4;

-- ============================================================
-- COURSE CATEGORIES
-- Replaces the old CourseApplication.category enum with catalogue records.
-- internal_training controls whether a category behaves as internal training.
-- ============================================================
INSERT IGNORE INTO course_categories
(id, code, name, description, internal_training, active) VALUES
(1, 'INTERNAL',      'Internal Training',           'Training conducted internally by the organisation.', b'1', b'1'),
(2, 'EXTERNAL',      'External Course',             'External training course provided by a training provider.', b'0', b'1'),
(3, 'CERTIFICATION', 'Professional Certification',  'Professional certification or certification-related training.', b'0', b'1');

-- ============================================================
-- TRAINING PROVIDERS
-- Providers used by the original demo course applications.
-- ============================================================
INSERT IGNORE INTO training_providers
(id, name, description, active) VALUES
(1, 'NUS-ISS',        'NUS Institute of Systems Science', b'1'),
(2, 'NUS SDZ Club',   'NUS SDZ Club',                     b'1'),
(3, 'NUS Music Club', 'NUS Music Club',                   b'1');
 
-- ============================================================
-- ADDITIONAL TRAINING PROVIDERS
-- ============================================================
INSERT IGNORE INTO training_providers
(id, name, description, active) VALUES
(101, 'AWS Training',          'Cloud training and certification programmes.', b'1'),
(102, 'Microsoft Learn',       'Microsoft technical training programmes.',     b'1'),
(103, 'Google Cloud Training', 'Google Cloud learning programmes.',            b'1'),
(104, 'Scrum.org',             'Agile and Scrum professional training.',       b'1'),
(105, 'Internal Academy',      'Organisation internal learning academy.',      b'1');

-- ============================================================
-- COURSES
-- One catalogue course is created for each original demo application course.
-- category_id: 1=INTERNAL, 2=EXTERNAL, 3=CERTIFICATION
-- provider_id: 1=NUS-ISS, 2=NUS SDZ Club, 3=NUS Music Club
-- ============================================================
INSERT IGNORE INTO courses
(id, code, title, description, category_id, provider_id, location, fee, duration_days, active) VALUES
(1,  'JAVA-ADV',       'Java Advanced',                  'Advanced Java training.',                    2, 1, NULL, 250.00, 2.0, b'1'),
(2,  'JAVAEE-ADV',     'JavaEE Advanced',                'Advanced Java EE training.',                 1, 1, NULL,   0.00, 0.5, b'1'),
(3,  'JAVA-FUND',      'Java Fundamentals',              'Java fundamentals training.',                2, 1, NULL, 250.00, 2.0, b'1'),
(4,  'CHINESE-BASIC',  'Basic Chinese Lang',             'Basic Chinese language training.',           3, 1, NULL, 100.00, 2.0, b'1'),
(5,  'JAPANESE-LANG',  'Japanese Lang',                  'Japanese language training.',                3, 1, NULL,  50.00, 2.0, b'1'),
(6,  'SQL-BEGINNER',   'SQL for Beginners',              'Introductory SQL training.',                 1, 1, NULL,   0.00, 0.5, b'1'),
(7,  'ML-ADV',         'Advanced Machine Learning',       'Advanced machine learning training.',        2, 1, NULL,  50.00, 2.0, b'1'),
(8,  'BREAKDANCE-BEG', 'Breakdance for Beginners',       'Beginner breakdance course.',                3, 2, NULL,  50.00, 3.0, b'1'),
(9, 'HOLIDAY-TEST', 'Holiday counting test', 			 'Demo course used for holiday counting.',	   1, 1, NULL,   0.00, 0.5, b'1'),
(10, 'VOCAL-BASIC',    'Basic Vocal Course',             'Basic vocal training.',                      2, 3, NULL, 250.00, 4.0, b'1'),
(11, 'AI-PROMPT-FUND', 'Fundamentals on AI Prompting',   'Fundamentals of AI prompting.',              1, 1, NULL,   0.00, 0.5, b'1');

-- ============================================================
-- ADDITIONAL COURSES
-- ============================================================
INSERT IGNORE INTO courses
(id, code, title, description, category_id, provider_id, location, fee, duration_days, active) VALUES
(101, 'AWS-CP',       'AWS Cloud Practitioner',        'Foundation cloud course.',                2, 101, 'Singapore', 1200.00, 3.0, b'1'),
(102, 'AZ-900',       'Microsoft Azure Fundamentals',  'Introduction to Microsoft Azure.',        3, 102, 'Online',     650.00, 2.0, b'1'),
(103, 'GCP-FUND',     'Google Cloud Fundamentals',     'Foundation Google Cloud training.',       2, 103, 'Online',     900.00, 2.0, b'1'),
(104, 'PSM-I',        'Professional Scrum Master I',   'Scrum Master certification preparation.', 3, 104, 'Singapore',  800.00, 2.0, b'1'),
(105, 'SPRING-INT',   'Spring Boot Internal Workshop','Hands-on internal Spring Boot workshop.',  1, 105, 'Office',       0.00, 1.0, b'1'),
(106, 'SEC-AWARE',    'Cybersecurity Awareness',       'Internal cybersecurity awareness.',       1, 105, 'Office',       0.00, 0.5, b'1'),
(107, 'LEAD-101',     'Leadership Essentials',         'Practical leadership fundamentals.',      2, 1,   'Singapore',  500.00, 2.0, b'1'),
(108, 'DATA-VIZ',     'Data Visualisation Essentials', 'Data storytelling and visualisation.',     2, 1,   'Singapore',  450.00, 2.0, b'1'),
(109, 'AGILE-INT',    'Agile Ways of Working',         'Internal agile practices workshop.',       1, 105, 'Office',       0.00, 1.0, b'1');

-- ============================================================
-- COURSE APPLICATIONS
-- Preserve the original application IDs, dates, statuses and history.
-- The legacy category field is no longer used. Each application now has
-- a valid course_id that points to the current Course entity.
-- ============================================================
INSERT IGNORE INTO course_application
(id, course_title, training_provider, start_date, end_date, duration_days, fee,
 justification, work_dissemination, status, manager_reason, experience_comment,
 decision_date, decided_by_id, employee_id, course_id) VALUES
(1,  'Java Advanced',                'NUS-ISS',        '2026-10-05', '2026-10-06', 2.00, 250.00, 'Improve Java skills',                    '', 'APPROVED',  'Part of staff ROA',                                  NULL,                         '2026-10-01 23:52:44.938513', 2,    1, 1),
(2,  'JavaEE Advanced',              'NUS-ISS',        '2026-09-30', '2026-09-30', 0.50,   0.00, 'Improve skills  ',                       '', 'COMPLETED', NULL,                                                 'I learned SpringBoot :)',    NULL,                         NULL, 1, 2),
(3,  'Java Fundamentals',            'NUS-ISS',        '2026-10-12', '2026-10-13', 2.00, 250.00, 'Improve Java skills',                    '', 'DELETED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 3),
(4,  'Basic Chinese Lang',           'NUS-ISS',        '2026-10-26', '2026-10-27', 2.00, 100.00, 'Learn basic chinese',                    '', 'CANCELLED', NULL,                                                 NULL,                         NULL,                         NULL, 1, 4),
(5,  'Japanese Lang',                'NUS-ISS',        '2026-11-10', '2026-11-11', 2.00,  50.00, 'Communicate with Japanese client',       '', 'REJECTED',  'No other employees around during the same period',    NULL,                         '2026-10-01 23:58:40.018247', 2,    1, 5),
(6,  'SQL for Beginners',            'NUS-ISS',        '2026-11-04', '2026-11-04', 0.50,   0.00, 'Test internal training fee',             '', 'UPDATED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 6),
(7,  'Advanced Machine Learning',     'NUS-ISS',        '2026-10-29', '2026-10-30', 2.00,  50.00, 'Testingggggggggggggggggg',               '', 'APPLIED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 7),
(8,  'Breakdance for Beginners',     'NUS SDZ Club',   '2026-11-11', '2026-11-13', 3.00,  50.00, 'Testing part 3',                         '', 'APPLIED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 8),
(9,  'Holiday counting test',        'NUS-ISS',        '2026-10-20', '2026-10-20', 0.50,   0.00, 'Testing holiday exclusion',              '', 'DELETED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 9),
(10, 'Basic Vocal Course',           'NUS Music Club', '2026-10-12', '2026-10-15', 4.00, 250.00, 'Interests',                              '', 'APPROVED',  'No conflict of intere–st',                            NULL,                         '2026-10-02 13:36:21.857825', 2,    3, 10),
(11, 'Fundamentals on AI Prompting', 'NUS-ISS',        '2026-10-14', '2026-10-14', 0.50,   0.00, 'To understand more on AI prompting',     '', 'APPLIED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 11);

-- ============================================================
-- EXTENDED COURSE APPLICATIONS
-- Mix of statuses/categories/employees to make manager views, reports,
-- dashboards and CSV exports interesting. All rows have valid course_id.
-- ============================================================
INSERT IGNORE INTO course_application
(id, course_title, training_provider, start_date, end_date, duration_days, fee,
 justification, work_dissemination, status, manager_reason, experience_comment,
 decision_date, decided_by_id, employee_id, course_id) VALUES
(101, 'AWS Cloud Practitioner',         'AWS Training',          '2026-10-19', '2026-10-21', 3.00, 1200.00, 'Build cloud fundamentals.',           'Share cloud notes with the team.',       'APPROVED',  'Relevant to current projects.', NULL, '2026-10-08 09:15:00', 103, 102, 101),
(102, 'Spring Boot Internal Workshop',  'Internal Academy',      '2026-11-02', '2026-11-02', 1.00,    0.00, 'Improve backend development skills.',  'Run a short knowledge-sharing session.', 'APPLIED',   NULL, NULL, NULL, NULL, 102, 105),
(103, 'Professional Scrum Master I',    'Scrum.org',             '2026-11-09', '2026-11-10', 2.00,  800.00, 'Improve agile delivery skills.',       'Apply Scrum practices to team planning.','UPDATED',   NULL, NULL, NULL, NULL, 105, 104),
(104, 'Cybersecurity Awareness',        'Internal Academy',      '2026-09-18', '2026-09-18', 0.50,    0.00, 'Improve security awareness.',          'Share key security reminders.',          'COMPLETED', 'Approved for development.', 'Useful practical reminders.', '2026-09-01 10:00:00', 106, 105, 106),
(105, 'Leadership Essentials',          'NUS-ISS',               '2026-10-22', '2026-10-23', 2.00,  500.00, 'Prepare for future leadership work.',  'Share leadership techniques.',           'APPROVED',  'Supports development plan.', NULL, '2026-10-09 11:30:00', 109, 108, 107),
(106, 'Data Visualisation Essentials',  'NUS-ISS',               '2026-11-16', '2026-11-17', 2.00,  450.00, 'Improve reporting skills.',            'Use techniques in monthly reporting.',   'REJECTED',  'Training budget prioritised elsewhere.', NULL, '2026-10-10 14:00:00', 109, 108, 108),
(107, 'Microsoft Azure Fundamentals',   'Microsoft Learn',       '2026-10-26', '2026-10-27', 2.00,  650.00, 'Learn Azure fundamentals.',            'Document key Azure services.',            'APPROVED',  'Relevant technical development.', NULL, '2026-10-11 09:30:00', 112, 111, 102),
(108, 'Agile Ways of Working',          'Internal Academy',      '2026-12-01', '2026-12-01', 1.00,    0.00, 'Improve team collaboration.',          'Apply agile practices in projects.',      'APPLIED',   NULL, NULL, NULL, NULL, 111, 109),
(109, 'Google Cloud Fundamentals',      'Google Cloud Training', '2026-09-14', '2026-09-15', 2.00,  900.00, 'Broaden cloud knowledge.',             'Compare cloud platforms with team.',      'COMPLETED', 'Approved for cross-skilling.', 'Good overview of GCP services.', '2026-08-28 16:00:00', 115, 114, 103),
(110, 'Cybersecurity Awareness',        'Internal Academy',      '2026-10-30', '2026-10-30', 0.50,    0.00, 'Security refresher.',                   'Share security checklist.',               'APPROVED',  'Mandatory awareness training.', NULL, '2026-10-12 10:00:00', 115, 114, 106),
(111, 'Professional Scrum Master I',    'Scrum.org',             '2026-11-23', '2026-11-24', 2.00,  800.00, 'Prepare for Scrum certification.',      'Coach team on Scrum ceremonies.',         'CANCELLED', 'Approved initially.', NULL, '2026-10-13 13:00:00', 118, 117, 104),
(112, 'AWS Cloud Practitioner',         'AWS Training',          '2026-12-07', '2026-12-09', 3.00, 1200.00, 'Strengthen cloud foundation.',          'Share AWS architecture notes.',           'APPLIED',   NULL, NULL, NULL, NULL, 117, 101),
(113, 'Data Visualisation Essentials',  'NUS-ISS',               '2026-10-19', '2026-10-20', 2.00,  450.00, 'Improve dashboard design.',             'Improve internal reporting dashboards.',  'APPROVED',  'Useful for reporting responsibilities.', NULL, '2026-10-08 15:30:00', 121, 120, 108),
(114, 'Leadership Essentials',          'NUS-ISS',               '2026-08-20', '2026-08-21', 2.00,  500.00, 'Develop stakeholder skills.',           'Apply techniques in project meetings.',   'COMPLETED', 'Approved for development.', 'Very useful leadership exercises.', '2026-08-01 09:00:00', 121, 120, 107),
(115, 'Spring Boot Internal Workshop',  'Internal Academy',      '2026-11-05', '2026-11-05', 1.00,    0.00, 'Improve Java backend skills.',          'Share examples with development team.',   'APPROVED',  'Relevant to application work.', NULL, '2026-10-15 10:45:00', 124, 123, 105),
(116, 'Microsoft Azure Fundamentals',   'Microsoft Learn',       '2026-12-14', '2026-12-15', 2.00,  650.00, 'Understand Azure services.',            'Create internal Azure reference notes.',  'UPDATED',   NULL, NULL, NULL, NULL, 123, 102),
(117, 'Agile Ways of Working',          'Internal Academy',      '2026-10-15', '2026-10-15', 1.00,    0.00, 'Improve agile collaboration.',          'Use retrospective techniques.',           'APPROVED',  'Supports team collaboration.', NULL, '2026-10-09 16:00:00', 106, 105, 109),
(118, 'Google Cloud Fundamentals',      'Google Cloud Training', '2026-11-12', '2026-11-13', 2.00,  900.00, 'Explore another cloud platform.',       'Present comparison to engineering team.', 'REJECTED',  'Not currently required.', NULL, '2026-10-16 09:00:00', 103, 102, 103),
(119, 'Leadership Essentials',          'NUS-ISS',               '2026-11-19', '2026-11-20', 2.00,  500.00, 'Develop people-management skills.',      'Apply coaching methods with team.',        'APPROVED',  'Supports manager development.', NULL, '2026-10-20 09:00:00', NULL, 103, 107),
(120, 'Professional Scrum Master I',    'Scrum.org',             '2026-12-03', '2026-12-04', 2.00,  800.00, 'Refresh agile leadership skills.',       'Share Scrum improvements.',               'APPROVED',  'Supports programme delivery.', NULL, '2026-10-21 11:00:00', NULL, 112, 104);

-- ============================================================
-- COURSE FEE CLAIM DEMO APPLICATIONS
-- Completed External Course / Professional Certification
-- applications used to demonstrate reimbursement workflows.
--
-- Application 203 deliberately has no claim so that a new
-- reimbursement claim can be submitted through the UI.
-- ============================================================

INSERT IGNORE INTO course_application
(id, course_title, training_provider, start_date, end_date,
 duration_days, fee, justification, work_dissemination,
 status, manager_reason, experience_comment,
 decision_date, decided_by_id, employee_id, course_id)
VALUES

-- ------------------------------------------------------------
-- Chloe Employee (108), supervisor = Chloe Manager (109)
-- ------------------------------------------------------------

(201,
 'Leadership Essentials',
 'NUS-ISS',
 '2026-02-09',
 '2026-02-10',
 2.00,
 500.00,
 'Develop leadership and communication skills.',
 'Share leadership techniques with colleagues.',
 'COMPLETED',
 'Supports professional development.',
 'Useful practical leadership exercises.',
 '2026-01-20 10:00:00',
 109,
 108,
 107),

(202,
 'Data Visualisation Essentials',
 'NUS-ISS',
 '2026-03-16',
 '2026-03-17',
 2.00,
 450.00,
 'Improve reporting and data visualisation skills.',
 'Apply visualisation techniques to team dashboards.',
 'COMPLETED',
 'Relevant to reporting responsibilities.',
 'Useful techniques for presenting data clearly.',
 '2026-02-20 11:00:00',
 109,
 108,
 108),

-- No CourseFeeClaim is created for application 203.
-- Use chloe2 to test submitting a new reimbursement claim.

(203,
 'Microsoft Azure Fundamentals',
 'Microsoft Learn',
 '2026-04-13',
 '2026-04-14',
 2.00,
 650.00,
 'Develop Azure platform knowledge.',
 'Prepare an internal Azure reference guide.',
 'COMPLETED',
 'Supports technical development.',
 'Useful overview of Azure services.',
 '2026-03-18 14:00:00',
 109,
 108,
 102),


-- ------------------------------------------------------------
-- Syahmul Employee (102), supervisor = Syahmul Manager (103)
-- ------------------------------------------------------------

(204,
 'Google Cloud Fundamentals',
 'Google Cloud Training',
 '2026-05-18',
 '2026-05-19',
 2.00,
 900.00,
 'Broaden cloud platform knowledge.',
 'Share a cloud platform comparison with the team.',
 'COMPLETED',
 'Supports cross-platform technical development.',
 'Good overview of Google Cloud services.',
 '2026-04-20 09:30:00',
 103,
 102,
 103),

(205,
 'Leadership Essentials',
 'NUS-ISS',
 '2026-06-15',
 '2026-06-16',
 2.00,
 500.00,
 'Develop leadership and communication skills.',
 'Share leadership techniques with colleagues.',
 'COMPLETED',
 'Supports professional development.',
 'Useful practical leadership exercises.',
 '2026-05-20 10:30:00',
 103,
 102,
 107),


-- ------------------------------------------------------------
-- Hafizah Employee (105), supervisor = Hafizah Manager (106)
-- ------------------------------------------------------------

(206,
 'Professional Scrum Master I',
 'Scrum.org',
 '2026-07-13',
 '2026-07-14',
 2.00,
 800.00,
 'Improve agile delivery knowledge.',
 'Apply Scrum practices to team planning.',
 'COMPLETED',
 'Supports professional development.',
 'Useful preparation for Scrum certification.',
 '2026-06-18 15:00:00',
 106,
 105,
 104);
 
 -- ============================================================
-- COURSE FEE CLAIM DEMO DATA
--
-- Provides examples of every Course Fee Claim workflow state:
-- PENDING, APPROVED, REJECTED and REIMBURSED.
--
-- Application 203 deliberately has no claim.
-- ============================================================

INSERT IGNORE INTO course_fee_claim
(id, application_id, amount, claim_date,
 receipt_path, certificate_path,
 status, manager_reason, decided_by, decision_date)
VALUES

-- ------------------------------------------------------------
-- Chloe Employee
-- ------------------------------------------------------------

-- PENDING:
-- Chloe Manager (chloe3) can review this claim.
(201,
 201,
 500.00,
 '2026-02-11 09:00:00',
 'uploads/claims/demo-receipt-201.pdf',
 'uploads/claims/demo-certificate-201.pdf',
 'PENDING',
 NULL,
 NULL,
 NULL),

-- APPROVED:
-- Chloe Employee (chloe2) can test "Mark as reimbursed".
(202,
 202,
 450.00,
 '2026-03-18 10:00:00',
 'uploads/claims/demo-receipt-202.pdf',
 'uploads/claims/demo-certificate-202.pdf',
 'APPROVED',
 'Receipt and certificate verified. Reimbursement approved.',
 109,
 '2026-03-20 14:30:00'),


-- ------------------------------------------------------------
-- Syahmul Employee
-- ------------------------------------------------------------

-- REIMBURSED:
-- Demonstrates the completed reimbursement lifecycle.
(204,
 204,
 900.00,
 '2026-05-20 10:15:00',
 'uploads/claims/demo-receipt-204.pdf',
 'uploads/claims/demo-certificate-204.pdf',
 'REIMBURSED',
 'Supporting documents verified and reimbursement approved.',
 103,
 '2026-05-22 16:00:00'),

-- PENDING:
-- Syahmul Manager (syahmul3) can review this claim.
(205,
 205,
 500.00,
 '2026-06-17 09:45:00',
 'uploads/claims/demo-receipt-205.pdf',
 'uploads/claims/demo-certificate-205.pdf',
 'PENDING',
 NULL,
 NULL,
 NULL),


-- ------------------------------------------------------------
-- Hafizah Employee
-- ------------------------------------------------------------

-- REJECTED:
-- Demonstrates historical rejected claim details.
(206,
 206,
 800.00,
 '2026-07-15 09:30:00',
 'uploads/claims/demo-receipt-206.pdf',
 'uploads/claims/demo-certificate-206.pdf',
 'REJECTED',
 'Receipt does not provide sufficient proof of payment.',
 106,
 '2026-07-17 11:00:00');
 
 
-- ============================================================
-- TRAINING ALLOWANCES
-- Preserve the original allowance records.
-- ============================================================
INSERT IGNORE INTO training_allowance
(id, day_limit, fee_budget, year, employee_id) VALUES
(1, 10.00, 2000.00, 2026, 1),
(2, 10.00, NULL,    2027, 1),
(3, 10.00, 2000.00, 2026, 3);

-- ============================================================
-- EXTENDED TRAINING ALLOWANCES (2026)
-- Gives the new demo profiles meaningful dashboard/report values.
-- ============================================================
INSERT IGNORE INTO training_allowance
(id, day_limit, fee_budget, year, employee_id) VALUES
(101, 10.00, 3000.00, 2026, 101), (102, 10.00, 3500.00, 2026, 102), (103, 12.00, 4000.00, 2026, 103),
(104, 10.00, 3000.00, 2026, 104), (105,  8.00, 2200.00, 2026, 105), (106, 12.00, 3500.00, 2026, 106),
(107, 10.00, 2800.00, 2026, 107), (108, 10.00, 2400.00, 2026, 108), (109, 12.00, 3600.00, 2026, 109),
(110, 10.00, 3000.00, 2026, 110), (111,  9.00, 2500.00, 2026, 111), (112, 12.00, 3800.00, 2026, 112),
(113, 10.00, 3000.00, 2026, 113), (114, 10.00, 2600.00, 2026, 114), (115, 12.00, 4000.00, 2026, 115),
(116, 10.00, 3000.00, 2026, 116), (117, 10.00, 3000.00, 2026, 117), (118, 12.00, 4200.00, 2026, 118),
(119, 10.00, 3000.00, 2026, 119), (120,  8.00, 2200.00, 2026, 120), (121, 12.00, 3500.00, 2026, 121),
(122, 10.00, 3000.00, 2026, 122), (123, 10.00, 2700.00, 2026, 123), (124, 12.00, 3900.00, 2026, 124),
(125, 0.00, 0.00, 2026, 125);

-- ============================================================
-- PUBLIC HOLIDAY
-- Preserve the existing demo holiday.
-- ============================================================
INSERT IGNORE INTO public_holiday (id, date, name) VALUES
(1, '2026-01-01', 'New Year');

-- Additional public holidays for richer calendar/working-day tests.
INSERT IGNORE INTO public_holiday (id, date, name) VALUES
(101, '2026-05-01', 'Labour Day'),
(102, '2026-08-09', 'National Day'),
(103, '2026-12-25', 'Christmas Day');

SET FOREIGN_KEY_CHECKS = 1;
