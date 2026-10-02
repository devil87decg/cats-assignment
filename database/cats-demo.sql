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

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `cats` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `cats`;

--
-- Table structure for table `course_application`
--

DROP TABLE IF EXISTS `course_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` enum('CERTIFICATION','EXTERNAL','INTERNAL') DEFAULT NULL,
  `course_title` varchar(255) DEFAULT NULL,
  `decision_date` datetime(6) DEFAULT NULL,
  `duration_days` decimal(38,2) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `experience_comment` varchar(255) DEFAULT NULL,
  `fee` decimal(38,2) DEFAULT NULL,
  `half_day` bit(1) NOT NULL,
  `justification` varchar(255) DEFAULT NULL,
  `manager_reason` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` enum('APPLIED','APPROVED','CANCELLED','COMPLETED','DELETED','REJECTED','UPDATED') DEFAULT NULL,
  `training_provider` varchar(255) DEFAULT NULL,
  `work_dissemination` varchar(255) DEFAULT NULL,
  `decided_by_id` bigint DEFAULT NULL,
  `employee_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlb08ut76f3d43v76pdkx2kqdt` (`decided_by_id`),
  KEY `FKmvavsyoh37lgp223baddllrl8` (`employee_id`),
  CONSTRAINT `FKlb08ut76f3d43v76pdkx2kqdt` FOREIGN KEY (`decided_by_id`) REFERENCES `employees` (`id`),
  CONSTRAINT `FKmvavsyoh37lgp223baddllrl8` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_application`
--

LOCK TABLES `course_application` WRITE;
/*!40000 ALTER TABLE `course_application` DISABLE KEYS */;
INSERT INTO `course_application` VALUES (1,'EXTERNAL','Java Advanced','2026-10-01 23:52:44.938513',2.00,'2026-10-06',NULL,250.00,_binary '\0','Improve Java skills','Part of staff ROA','2026-10-05','APPROVED','NUS-ISS','',2,1),(2,'INTERNAL','JavaEE Advanced',NULL,0.50,'2026-09-30','I learned SpringBoot :)',0.00,_binary '','Improve skills  ',NULL,'2026-09-30','COMPLETED','NUS-ISS','',NULL,1),(3,'EXTERNAL','Java Fundamentals',NULL,2.00,'2026-10-13',NULL,250.00,_binary '\0','Improve Java skills',NULL,'2026-10-12','DELETED','NUS-ISS','',NULL,1),(4,'CERTIFICATION','Basic Chinese Lang',NULL,2.00,'2026-10-27',NULL,100.00,_binary '\0','Learn basic chinese',NULL,'2026-10-26','CANCELLED','NUS-ISS','',NULL,1),(5,'CERTIFICATION','Japanese Lang','2026-10-01 23:58:40.018247',2.00,'2026-11-11',NULL,50.00,_binary '\0','Communicate with Japanese client','No other employees around during the same period','2026-11-10','REJECTED','NUS-ISS','',2,1),(6,'INTERNAL','SQL for Beginners',NULL,0.50,'2026-11-04',NULL,0.00,_binary '','Test internal training fee',NULL,'2026-11-04','UPDATED','NUS-ISS','',NULL,1),(7,'EXTERNAL','Advanced Machine Learning',NULL,2.00,'2026-10-30',NULL,50.00,_binary '\0','Testingggggggggggggggggg',NULL,'2026-10-29','APPLIED','NUS-ISS','',NULL,1),(8,'CERTIFICATION','Breakdance for Beginners',NULL,3.00,'2026-11-13',NULL,50.00,_binary '\0','Testing part 3',NULL,'2026-11-11','APPLIED','NUS SDZ Club','',NULL,1),(9,'INTERNAL','Holiday counting test',NULL,2.00,'2026-10-22',NULL,0.00,_binary '\0','Testing holiday exclusion',NULL,'2026-10-20','DELETED','NUS-ISS','',NULL,1),(10,'EXTERNAL','Basic Vocal Course','2026-10-02 13:36:21.857825',4.00,'2026-10-15',NULL,250.00,_binary '\0','Interests','No conflict of interest','2026-10-12','APPROVED','NUS Music Club','',2,3),(11,'INTERNAL','Fundamentals on AI Prompting ',NULL,1.00,'2026-10-14',NULL,0.00,_binary '\0','To understand more on AI prompting',NULL,'2026-10-14','APPLIED','NUS-ISS','',NULL,1);
/*!40000 ALTER TABLE `course_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department` varchar(255) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `staff_category` enum('ADMINISTRATIVE','PROFESSIONAL') DEFAULT NULL,
  `supervisor_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKj2dmgsma6pont6kf7nic9elpd` (`user_id`),
  KEY `FK2q9drrvhye5m5ivk5h6re2b4i` (`supervisor_id`),
  CONSTRAINT `FK2q9drrvhye5m5ivk5h6re2b4i` FOREIGN KEY (`supervisor_id`) REFERENCES `employees` (`id`),
  CONSTRAINT `FK69x3vjuy1t5p18a5llb8h2fjx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (1,'','','xiang xuan','PROFESSIONAL',2,2),(2,'IT ','Manager','Dogbert','PROFESSIONAL',NULL,3),(3,'Admin Staff','Secretary','Ratbert','PROFESSIONAL',2,4),(4,'Finance','Accounts Manager','Kuan Yew','PROFESSIONAL',NULL,5);
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `public_holiday`
--

DROP TABLE IF EXISTS `public_holiday`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `public_holiday` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfpqh0byona3oh9qf4ymr0l5cb` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `public_holiday`
--

LOCK TABLES `public_holiday` WRITE;
/*!40000 ALTER TABLE `public_holiday` DISABLE KEYS */;
INSERT INTO `public_holiday` VALUES (1,'2026-10-21','TEST holiday - remove after testing');
/*!40000 ALTER TABLE `public_holiday` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_allowance`
--

DROP TABLE IF EXISTS `training_allowance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_allowance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_limit` decimal(38,2) DEFAULT NULL,
  `fee_budget` decimal(38,2) DEFAULT NULL,
  `year` int NOT NULL,
  `employee_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2ox84f7o2cgfw6g3gjrqcp46p` (`employee_id`,`year`),
  CONSTRAINT `FKgvecglf5jjfir1b5qfvi9es10` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_allowance`
--

LOCK TABLES `training_allowance` WRITE;
/*!40000 ALTER TABLE `training_allowance` DISABLE KEYS */;
INSERT INTO `training_allowance` VALUES (1,10.00,2000.00,2026,1),(2,10.00,NULL,2027,1),(3,10.00,2000.00,2026,3);
/*!40000 ALTER TABLE `training_allowance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `active` bit(1) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','EMPLOYEE','MANAGER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin',_binary '','$2a$10$ujWik98HXV4LRhHycVP/QOSG2x9K.M5EElZkuShmWmyYhP18NMcM.','ADMIN'),(2,'xiang xuan',_binary '','$2a$10$HSjlB9suCskul1SVJ2l2C.ATwSzgp1bL3tBUof.jd5XSOdmgu5NPC','EMPLOYEE'),(3,'manager1',_binary '','$2a$10$4jL0fqmHITYe2ixzdR2p/eLvHP1i6ee1wyDsAetftch/p/Pb5VtEy','MANAGER'),(4,'employee2',_binary '','$2a$10$k585OedWPUdUdV/3lcHrCeqzjs3rnsbrsZUbk3pJ63TRCRie.6Zy6','EMPLOYEE'),(5,'manager2',_binary '','$2a$10$UaQQjacpa3TmoL29Crfxfe8117Rm6Ouw3Lk9W9LWSnCsnU1Y1YT0u','MANAGER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-02 17:45:08
