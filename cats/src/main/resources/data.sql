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

--
-- Current Database: `cats`
--

SET FOREIGN_KEY_CHECKS = 0;
--
-- Table structure for table `employees`
--


INSERT INTO `employees` 
(id, department, designation, name, staff_category, supervisor_id, user_id, email)
VALUES 
(1,'','','xiang xuan','PROFESSIONAL',2,2, 'xin.xian.quek@gmail.com'),
(2,'IT ','Manager','Dogbert','PROFESSIONAL',NULL,3, 'dogbert@example.com'),
(3,'Admin Staff','Secretary','Ratbert','PROFESSIONAL',2,4, 'ratbert@example.com'),
(4,'Finance','Accounts Manager','Kuan Yew','PROFESSIONAL',NULL,5, 'kuan_yew@example.com');
--
-- Table structure for table `course_application`
--

INSERT INTO `course_application` 
(id, category, course_title, decision_date, duration_days, end_date, experience_comment, fee, half_day, justification, manager_reason, start_date, status, training_provider, work_dissemination, decided_by_id, employee_id)
VALUES 
(1,'EXTERNAL','Java Advanced','2026-10-01 23:52:44.938513',2.00,'2026-10-06',NULL,250.00,_binary '\0','Improve Java skills','Part of staff ROA','2026-10-05','APPROVED','NUS-ISS','',2,1),
(2,'INTERNAL','JavaEE Advanced',NULL,0.50,'2026-09-30','I learned SpringBoot :)',0.00,_binary '','Improve skills  ',NULL,'2026-09-30','COMPLETED','NUS-ISS','',NULL,1),
(3,'EXTERNAL','Java Fundamentals',NULL,2.00,'2026-10-13',NULL,250.00,_binary '\0','Improve Java skills',NULL,'2026-10-12','DELETED','NUS-ISS','',NULL,1),
(4,'CERTIFICATION','Basic Chinese Lang',NULL,2.00,'2026-10-27',NULL,100.00,_binary '\0','Learn basic chinese',NULL,'2026-10-26','CANCELLED','NUS-ISS','',NULL,1),
(5,'CERTIFICATION','Japanese Lang','2026-10-01 23:58:40.018247',2.00,'2026-11-11',NULL,50.00,_binary '\0','Communicate with Japanese client','No other employees around during the same period','2026-11-10','REJECTED','NUS-ISS','',2,1),
(6,'INTERNAL','SQL for Beginners',NULL,0.50,'2026-11-04',NULL,0.00,_binary '','Test internal training fee',NULL,'2026-11-04','UPDATED','NUS-ISS','',NULL,1),
(7,'EXTERNAL','Advanced Machine Learning',NULL,2.00,'2026-10-30',NULL,50.00,_binary '\0','Testingggggggggggggggggg',NULL,'2026-10-29','APPLIED','NUS-ISS','',NULL,1),
(8,'CERTIFICATION','Breakdance for Beginners',NULL,3.00,'2026-11-13',NULL,50.00,_binary '\0','Testing part 3',NULL,'2026-11-11','APPLIED','NUS SDZ Club','',NULL,1),
(9,'INTERNAL','Holiday counting test',NULL,2.00,'2026-10-22',NULL,0.00,_binary '\0','Testing holiday exclusion',NULL,'2026-10-20','DELETED','NUS-ISS','',NULL,1),
(10,'EXTERNAL','Basic Vocal Course','2026-10-02 13:36:21.857825',4.00,'2026-10-15',NULL,250.00,_binary '\0','Interests','No conflict of interest','2026-10-12','APPROVED','NUS Music Club','',2,3),
(11,'INTERNAL','Fundamentals on AI Prompting ',NULL,1.00,'2026-10-14',NULL,0.00,_binary '\0','To understand more on AI prompting',NULL,'2026-10-14','APPLIED','NUS-ISS','',NULL,1);
/*!40000 ALTER TABLE `course_application` ENABLE KEYS */;
UNLOCK TABLES;



INSERT INTO `public_holiday` 
(id, date, name)
VALUES (1,'2026-10-21','TEST holiday - remove after testing');


INSERT INTO `training_allowance`
(id, day_limit, fee_budget, year, employee_id)
VALUES (1,10.00,2000.00,2026,1),(2,10.00,NULL,2027,1),(3,10.00,2000.00,2026,3);


INSERT INTO `users` 
(id, username, active, password_hash, role)
VALUES (1,'admin',_binary '','$2a$10$ujWik98HXV4LRhHycVP/QOSG2x9K.M5EElZkuShmWmyYhP18NMcM.','ADMIN'),(2,'xiang xuan',_binary '','$2a$10$HSjlB9suCskul1SVJ2l2C.ATwSzgp1bL3tBUof.jd5XSOdmgu5NPC','EMPLOYEE'),(3,'manager1',_binary '','$2a$10$4jL0fqmHITYe2ixzdR2p/eLvHP1i6ee1wyDsAetftch/p/Pb5VtEy','MANAGER'),(4,'employee2',_binary '','$2a$10$k585OedWPUdUdV/3lcHrCeqzjs3rnsbrsZUbk3pJ63TRCRie.6Zy6','EMPLOYEE'),(5,'manager2',_binary '','$2a$10$UaQQjacpa3TmoL29Crfxfe8117Rm6Ouw3Lk9W9LWSnCsnU1Y1YT0u','MANAGER');
