
    
  
-- ============================================================
-- EXTENDED DEMO EMPLOYEES
-- Each *2 employee reports to the corresponding *3 manager.
-- Admin profiles are also represented so employee-management screens are rich.
-- ============================================================
INSERT IGNORE INTO employees
(id, department, designation, name, staff_category, supervisor_id, user_id, email) VALUES
(101, 'Administration', 'System Administrator',      'Syahmul Admin',    'ADMINISTRATIVE', NULL, 101, 'syahmul@example.com'),
(102, 'Technology',     'Software Engineer',         'Syahmul Employee', 'PROFESSIONAL',   103, 102, 'syahmul2@example.com'),
(103, 'Technology',     'Engineering Manager',       'Syahmul Manager',  'PROFESSIONAL',   NULL, 103, 'syahmul3@example.com'),
(104, 'Administration', 'HR Administrator',          'Hafizah Admin',    'ADMINISTRATIVE', NULL, 104, 'hafizah@example.com'),
(105, 'Human Resources','HR Executive',              'Hafizah Employee', 'PROFESSIONAL',   106, 105, 'hafizah2@example.com'),
(106, 'Human Resources','HR Manager',                'Hafizah Manager',  'PROFESSIONAL',   NULL, 106, 'hafizah3@example.com'),
(107, 'Administration', 'Operations Administrator',  'Chloe Admin',      'ADMINISTRATIVE', NULL, 107, 'chloe@example.com'),
(108, 'Operations',     'Operations Analyst',        'Chloe Employee',   'PROFESSIONAL',   109, 108, 'chloe2@example.com'),
(109, 'Operations',     'Operations Manager',        'Chloe Manager',    'PROFESSIONAL',   NULL, 109, 'chloe3@example.com'),
(110, 'Administration', 'Programme Administrator',   'Aufa Admin',       'ADMINISTRATIVE', NULL, 110, 'aufa@example.com'),
(111, 'Programmes',     'Programme Executive',       'Aufa Employee',    'PROFESSIONAL',   112, 111, 'aufa2@example.com'),
(112, 'Programmes',     'Programme Manager',         'Aufa Manager',     'PROFESSIONAL',   NULL, 112, 'aufa3@example.com'),
(113, 'Administration', 'Finance Administrator',     'Xiuming Admin',    'ADMINISTRATIVE', NULL, 113, 'xiuming@example.com'),
(114, 'Finance',        'Finance Analyst',           'Xiuming Employee', 'PROFESSIONAL',   115, 114, 'xiuming2@example.com'),
(115, 'Finance',        'Finance Manager',           'Xiuming Manager',  'PROFESSIONAL',   NULL, 115, 'xiuming3@example.com'),
(116, 'Administration', 'IT Administrator',          'Xinxian Admin',    'ADMINISTRATIVE', NULL, 116, 'xinxian@example.com'),
(117, 'IT',             'Systems Analyst',           'Xinxian Employee', 'PROFESSIONAL',   118, 117, 'xinxian2@example.com'),
(118, 'IT',             'IT Manager',                'Xinxian Manager',  'PROFESSIONAL',   NULL, 118, 'xinxian3@example.com'),
(119, 'Administration', 'Learning Administrator',    'Ramesh Admin',     'ADMINISTRATIVE', NULL, 119, 'ramesh@example.com'),
(120, 'Learning',       'Learning Specialist',       'Ramesh Employee',  'PROFESSIONAL',   121, 120, 'ramesh2@example.com'),
(121, 'Learning',       'Learning Manager',          'Ramesh Manager',   'PROFESSIONAL',   NULL, 121, 'ramesh3@example.com'),
(122, 'Administration', 'Business Administrator',    'Dominic Admin',    'ADMINISTRATIVE', NULL, 122, 'dominic@example.com'),
(123, 'Business',       'Business Analyst',          'Dominic Employee', 'PROFESSIONAL',   124, 123, 'dominic2@example.com'),
(124, 'Business',       'Business Manager',          'Dominic Manager',  'PROFESSIONAL',   NULL, 124, 'dominic3@example.com');

-- ============================================================
-- COURSE CATEGORIES (required by courses.category_id)
-- These records must exist before inserting catalogue courses.
-- ============================================================
INSERT IGNORE INTO course_categories
(id, code, name, description, internal_training, active) VALUES
(1, 'INTERNAL', 'Internal Training', 'Training conducted internally by the organisation.', b'1', b'1'),
(2, 'EXTERNAL', 'External Course', 'External training course provided by a training provider.', b'0', b'1'),
(3, 'CERTIFICATION', 'Professional Certification', 'Professional certification or certification-related training.', b'0', b'1');

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
-- EXTENDED TRAINING ALLOWANCES (2026)
-- Gives the new demo profiles meaningful dashboard/report values.
-- ============================================================
INSERT IGNORE INTO training_allowance
(id, day_limit, fee_budget, year, employee_id) VALUES
(101, 10.00, 3000.00, 2026, 101), (102, 10.00, 2500.00, 2026, 102), (103, 12.00, 4000.00, 2026, 103),
(104, 10.00, 3000.00, 2026, 104), (105,  8.00, 2200.00, 2026, 105), (106, 12.00, 3500.00, 2026, 106),
(107, 10.00, 2800.00, 2026, 107), (108, 10.00, 2400.00, 2026, 108), (109, 12.00, 3600.00, 2026, 109),
(110, 10.00, 3000.00, 2026, 110), (111,  9.00, 2500.00, 2026, 111), (112, 12.00, 3800.00, 2026, 112),
(113, 10.00, 3000.00, 2026, 113), (114, 10.00, 2600.00, 2026, 114), (115, 12.00, 4000.00, 2026, 115),
(116, 10.00, 3000.00, 2026, 116), (117, 10.00, 3000.00, 2026, 117), (118, 12.00, 4200.00, 2026, 118),
(119, 10.00, 3000.00, 2026, 119), (120,  8.00, 2200.00, 2026, 120), (121, 12.00, 3500.00, 2026, 121),
(122, 10.00, 3000.00, 2026, 122), (123, 10.00, 2700.00, 2026, 123), (124, 12.00, 3900.00, 2026, 124);

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

-- Additional public holidays for richer calendar/working-day tests.
INSERT IGNORE INTO public_holiday (id, date, name) VALUES
(101, '2026-05-01', 'Labour Day'),
(102, '2026-08-09', 'National Day'),
(103, '2026-12-25', 'Christmas Day');

SET FOREIGN_KEY_CHECKS = 1;