-- MySQL dump 10.13  Distrib 9.7.2, for macos15 (arm64)
--
-- Host: localhost    Database: cats
-- ------------------------------------------------------
-- Server version	9.7.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

SET FOREIGN_KEY_CHECKS = 0;


-- ============================================================
-- EMPLOYEES
-- Preserve the original demo employees and reporting structure.
-- Manager/Dogbert is inserted first because other employees reference id 2.
-- ============================================================

INSERT IGNORE INTO `employees` 
(id, department, designation, name, staff_category, supervisor_id, user_id, email)
VALUES 
(1,'','','xiang xuan','PROFESSIONAL',2,2, 'devil.87.dec@gmail.com'),
(2,'IT ','Manager','Dogbert','PROFESSIONAL',NULL,3, 'xin.xian.quek@u.nus.edu'),
(3,'Admin Staff','Secretary','Ratbert','PROFESSIONAL',2,4, 'ratbert@example.com'),
(4,'Finance','Accounts Manager','Kuan Yew','PROFESSIONAL',NULL,5, 'kuan_yew@example.com');
--
-- Table structure for table `course_application`
--

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
(9,  'HOLIDAY-TEST',   'Holiday counting test',          'Demo course used for holiday counting.',     1, 1, NULL,   0.00, 2.0, b'1'),
(10, 'VOCAL-BASIC',    'Basic Vocal Course',             'Basic vocal training.',                      2, 3, NULL, 250.00, 4.0, b'1'),
(11, 'AI-PROMPT-FUND', 'Fundamentals on AI Prompting',   'Fundamentals of AI prompting.',              1, 1, NULL,   0.00, 1.0, b'1');

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
(9,  'Holiday counting test',        'NUS-ISS',        '2026-10-20', '2026-10-22', 2.00,   0.00, 'Testing holiday exclusion',              '', 'DELETED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 9),
(10, 'Basic Vocal Course',           'NUS Music Club', '2026-10-12', '2026-10-15', 4.00, 250.00, 'Interests',                              '', 'APPROVED',  'No conflict of interest',                            NULL,                         '2026-10-02 13:36:21.857825', 2,    3, 10),
(11, 'Fundamentals on AI Prompting', 'NUS-ISS',        '2026-10-14', '2026-10-14', 1.00,   0.00, 'To understand more on AI prompting',     '', 'APPLIED',   NULL,                                                 NULL,                         NULL,                         NULL, 1, 11);

-- ============================================================
-- PUBLIC HOLIDAY
-- Preserve the existing demo holiday.
-- ============================================================
INSERT IGNORE INTO public_holiday (id, date, name) VALUES
(1, '2026-01-01', 'New Year');

-- ============================================================
-- TRAINING ALLOWANCES
-- Preserve the original allowance records.
-- ============================================================
INSERT IGNORE INTO training_allowance
(id, day_limit, fee_budget, year, employee_id) VALUES
(1, 10.00, 2000.00, 2026, 1),
(2, 10.00, NULL,    2027, 1),
(3, 10.00, 2000.00, 2026, 3);

SET FOREIGN_KEY_CHECKS = 1;
