-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: springboot_patientdetails
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `medication`
--

DROP TABLE IF EXISTS `medication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medication` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `dosage` varchar(255) NOT NULL,
  `end_date` date NOT NULL,
  `frequency` varchar(255) NOT NULL,
  `medicine_name` varchar(255) NOT NULL,
  `patient_id` int NOT NULL,
  `reminder_time` time NOT NULL,
  `start_date` date NOT NULL,
  `sync_status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medication`
--

LOCK TABLES `medication` WRITE;
/*!40000 ALTER TABLE `medication` DISABLE KEYS */;
INSERT INTO `medication` VALUES (1,'2026-10-17 17:12:55.419159','500 mg','2026-10-22','Twice daily','Paracetamol',1,'08:00:00','2026-10-17','SYNCED'),(2,'2026-10-19 14:46:42.975638','500 mg','2026-10-22','Twice daily','Test Medicine',1,'08:00:00','2026-10-19','SYNCED'),(3,'2026-10-20 15:12:10.895316','500 mg','2026-10-25','Once daily','Paracetamol',47,'09:00:00','2026-10-20','SYNCED'),(4,'2026-10-20 15:58:23.073055','5mg','2026-10-24','ONCE_DAILY','Tablet1',51,'16:00:00','2026-10-20','SYNCED'),(5,'2026-10-20 15:59:49.622243','5mg','2026-10-24','ONCE_DAILY','Tablet1',51,'16:03:00','2026-10-20','SYNCED'),(6,'2026-10-20 16:13:15.791620','2 tablets','2026-10-24','TWICE_DAILY','Paracetamol',51,'16:14:00','2026-10-20','SYNCED'),(7,'2026-10-22 09:51:27.455761','1 tablet','2026-10-23','ONCE_DAILY','Paracetamol',53,'09:53:00','2026-10-22','SYNCED'),(8,'2026-10-22 09:57:24.058915','5mg','2026-10-23','ONCE_DAILY','Capsules',53,'09:59:00','2026-10-22','SYNCED'),(9,'2026-10-22 10:47:12.562382','1 tablet','2026-10-24','ONCE_DAILY','Paracetamol',54,'10:49:00','2026-10-22','SYNCED'),(10,'2026-10-24 12:32:44.299059','1 tablet','2026-10-30','Daily','Capsule',53,'10:30:00','2026-10-24','SYNCED'),(11,'2026-10-25 10:02:27.395257','5ml','2026-10-26','ONCE_DAILY','Cough Syrup',53,'10:04:00','2026-10-25','SYNCED');
/*!40000 ALTER TABLE `medication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medication_dose_log`
--

DROP TABLE IF EXISTS `medication_dose_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medication_dose_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `medication_id` int NOT NULL,
  `patient_id` int NOT NULL,
  `scheduled_date` date NOT NULL,
  `scheduled_time` time NOT NULL,
  `status` varchar(255) NOT NULL,
  `taken_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_medication_dose` (`medication_id`,`scheduled_date`,`scheduled_time`),
  UNIQUE KEY `uk_dose_medication_date_time` (`medication_id`,`scheduled_date`,`scheduled_time`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medication_dose_log`
--

LOCK TABLES `medication_dose_log` WRITE;
/*!40000 ALTER TABLE `medication_dose_log` DISABLE KEYS */;
INSERT INTO `medication_dose_log` VALUES (1,1,1,'2026-10-17','08:00:00','TAKEN','2026-10-19 10:20:25.911091','2026-10-19 10:20:25.911091'),(3,1,1,'2026-10-19','08:00:00','TAKEN','2026-10-19 15:13:57.231741','2026-10-19 15:13:57.231741'),(4,2,1,'2026-10-19','08:00:00','MISSED',NULL,'2026-10-24 11:57:06.019816'),(5,2,1,'2026-10-19','20:00:00','MISSED',NULL,'2026-10-24 11:57:06.019816'),(6,3,47,'2026-10-20','09:00:00','TAKEN','2026-10-20 15:17:38.939019','2026-10-20 15:17:38.939019'),(7,3,47,'2026-10-21','09:00:00','MISSED',NULL,'2026-10-20 15:25:48.286050'),(8,4,51,'2026-10-20','16:00:00','TAKEN','2026-10-20 16:12:10.010853','2026-10-20 16:12:10.010853'),(9,5,51,'2026-10-20','16:03:00','TAKEN','2026-10-20 16:12:10.484035','2026-10-20 16:12:10.484035'),(10,6,51,'2026-10-20','16:14:00','MISSED',NULL,'2026-10-20 16:17:00.129398'),(11,7,53,'2026-10-22','09:53:00','MISSED',NULL,'2026-10-22 09:53:24.457648'),(12,7,53,'2026-10-23','09:53:00','MISSED',NULL,'2026-10-24 19:20:27.448659'),(13,8,53,'2026-10-22','09:59:00','MISSED',NULL,'2026-10-24 19:20:27.448659'),(14,8,53,'2026-10-23','09:59:00','MISSED',NULL,'2026-10-24 19:20:27.448659'),(15,10,53,'2026-10-24','10:30:00','MISSED',NULL,'2026-10-24 19:20:27.448659'),(16,1,1,'2026-10-18','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(17,1,1,'2026-10-20','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(18,1,1,'2026-10-21','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(19,1,1,'2026-10-22','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(20,2,1,'2026-10-20','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(21,2,1,'2026-10-20','20:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(22,2,1,'2026-10-21','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(23,2,1,'2026-10-21','20:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(24,2,1,'2026-10-22','08:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(25,2,1,'2026-10-22','20:00:00','MISSED',NULL,'2026-10-25 08:41:21.426905'),(26,9,54,'2026-10-22','10:49:00','MISSED',NULL,'2026-10-25 08:42:55.550292'),(27,9,54,'2026-10-23','10:49:00','MISSED',NULL,'2026-10-25 08:42:55.550292'),(28,9,54,'2026-10-24','10:49:00','MISSED',NULL,'2026-10-25 08:42:55.550292'),(29,4,51,'2026-10-21','16:00:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(30,4,51,'2026-10-22','16:00:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(31,4,51,'2026-10-23','16:00:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(32,4,51,'2026-10-24','16:00:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(33,5,51,'2026-10-21','16:03:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(34,5,51,'2026-10-22','16:03:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(35,5,51,'2026-10-23','16:03:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(36,5,51,'2026-10-24','16:03:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(37,6,51,'2026-10-20','16:16:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(38,6,51,'2026-10-21','16:14:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(39,6,51,'2026-10-21','16:16:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(40,6,51,'2026-10-22','16:14:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(41,6,51,'2026-10-22','16:16:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(42,6,51,'2026-10-23','16:14:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(43,6,51,'2026-10-23','16:16:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(44,6,51,'2026-10-24','16:14:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(45,6,51,'2026-10-24','16:16:00','MISSED',NULL,'2026-10-25 08:43:19.885721'),(46,3,47,'2026-10-22','09:00:00','MISSED',NULL,'2026-10-25 10:00:42.314644'),(47,3,47,'2026-10-23','09:00:00','MISSED',NULL,'2026-10-25 10:00:42.314644'),(48,3,47,'2026-10-24','09:00:00','MISSED',NULL,'2026-10-25 10:00:42.314644'),(49,3,47,'2026-10-25','09:00:00','MISSED',NULL,'2026-10-25 13:42:21.223782'),(50,11,53,'2026-10-25','10:04:00','TAKEN','2026-10-25 10:04:24.644065','2026-10-25 10:04:24.644065');
/*!40000 ALTER TABLE `medication_dose_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medication_reminder_times`
--

DROP TABLE IF EXISTS `medication_reminder_times`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medication_reminder_times` (
  `medication_id` int NOT NULL,
  `reminder_time` time NOT NULL,
  KEY `FKks50fqyht13ru21dpdhxfhcjt` (`medication_id`),
  CONSTRAINT `FKks50fqyht13ru21dpdhxfhcjt` FOREIGN KEY (`medication_id`) REFERENCES `medication` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medication_reminder_times`
--

LOCK TABLES `medication_reminder_times` WRITE;
/*!40000 ALTER TABLE `medication_reminder_times` DISABLE KEYS */;
INSERT INTO `medication_reminder_times` VALUES (2,'08:00:00'),(2,'20:00:00'),(3,'09:00:00'),(4,'16:00:00'),(5,'16:03:00'),(6,'16:14:00'),(6,'16:16:00'),(7,'09:53:00'),(8,'09:59:00'),(9,'10:49:00'),(10,'10:30:00'),(11,'10:04:00');
/*!40000 ALTER TABLE `medication_reminder_times` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_entity`
--

DROP TABLE IF EXISTS `patient_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `age` int NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `sync_status` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcehu3pll779f0v15uwwdo3lol` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_entity`
--

LOCK TABLES `patient_entity` WRITE;
/*!40000 ALTER TABLE `patient_entity` DISABLE KEYS */;
INSERT INTO `patient_entity` VALUES (1,25,'Manipal','Rahul','9876543210','synched',NULL),(2,25,'Udupi','suri','9876543526','synched',NULL),(3,29,'Udupi','surith','9876543522','synched',NULL),(4,56,'Karkala','saniya','94848848484','synched',NULL),(5,54,'Manipal','devi','8484884848','synched',NULL),(6,54,'Karkala','frenie','848040756484','synched',NULL),(7,58,'Kundapura','kiya','54584884444','synched',NULL),(9,63,'Udupi','devima','8075050088','synched',NULL),(10,35,'Udupi','remi','9087085784','synched',NULL),(11,31,'Karkala','sari','8708408488','synched',NULL),(12,53,'Kundapura','kohli','7800788884','synched',NULL),(13,32,'Karkala','Tara','808788788','synched',NULL),(14,12,'Manipal','Shri','5870070078','synched',NULL),(15,12,'Udupi','Student ','804884668','synched',NULL),(16,29,'Kundapura','Dari','8078789488','synched',NULL),(17,65,'Udupi','Yuri','8078845456','synched',NULL),(18,56,'Karkala','Huji','8075456494','synched',NULL),(19,35,'Karkala','Reddy','9080888558','synched',NULL),(20,21,'Manipal','Tarun','9808708886','synched',NULL),(21,34,'Kundapura','Shahi','8075846545','synched',NULL),(22,27,'Manipal','Savi','9078879970','synched',NULL),(23,32,'Manipal','David','89704087823','synched',NULL),(24,35,'Udupi','Canva','9690807835','synched',NULL),(25,31,'Udupi','Tanu','805756782','synched',NULL),(26,78,'Manipal','Devi','907584654','synched',NULL),(27,32,'Karkala','Kali','636387089','synched',NULL),(28,32,'Udupi','Shahi','9089976555','synched',NULL),(29,12,'Udupi','Freny','807854695','synched',NULL),(30,61,'Karkala','Derryl','8708994554','synched',NULL),(31,34,'Karkala','Sharma','8709798765','synched',NULL),(32,12,'Manipal','Sarita','780809885','synched',NULL),(33,36,'Udupi','Remi','780875506','synched',NULL),(34,23,'Kundapura','Dhanush','9464548164545','synched',NULL),(35,50,'Karkala','Pushpa','98563247','synched',NULL),(36,46,'Manipal','Daya','7808458465','synched',NULL),(37,25,'Karkala','Riya','95874268','synched',NULL),(38,45,'Karkala','yuvi','4684854888','synched',NULL),(39,45,'Kundapura','sameer','87584848845','synched',NULL),(40,48,'Karkala','Mahima','9078789788','synched',NULL),(42,51,'Manipal','Aditi','87087979648','synced',NULL),(43,21,'Manipal','Tyla','8885857555','synced',NULL),(44,53,'Manipal','Tahin','64848849088','synced',NULL),(45,29,'Udupi','Daniel','54800789797','synced',NULL),(46,35,'Manipal','Tara','8078878878','synced',NULL),(47,25,'Manipal','Test Patient','8123654928','synced','$2a$10$//e2TSWMEV8nTwJwE8nYeuKlbnXQ8OKjegS1Vg53gz9YPIyxJQ4cK'),(48,32,'Manipal','Test Patient 1','9090909099','synced','$2a$10$wpWWYXkWvzaajbCKup969er.SCKbRb9f1uIdq/isIcYFYvpKP/aAC'),(49,21,'Manipal','Santa','8000800080','synced','$2a$10$bluBJdA9t3WYO0aMocXSiOt3v5ASRX5AjeZJu2pnJosMbrRD.ev1G'),(50,87,'Manipal','Abhi','9090909090','synced','$2a$10$/T8epfW1aoaqQddhH0XTRe/iopAcch7CDkzARLm5QjkJeZ8TXyU52'),(51,31,'Karkala','Gyana','7070707070','synced','$2a$10$gS8I0LHMZkHi8hLQOn5kseQJtlu5i5Rb.3YhjbN8ACXcZmsBvlV3e'),(52,23,'Kundapura','Kaushal','6060606060','synced','$2a$10$fEBvL9.OFD4mcsACyx6VkOT2Vk3FXe1ttyhH2HB/LPjm7xmU23cva'),(53,23,'Karkala','Jathin','8098098098','synced','$2a$10$S68K0vYg.X7G3wTDqmgYouu2im/xPnHygCY6p98psj5Wry.yZuhZi'),(54,45,'Manipal','Taman','9089089089','synced','$2a$10$j6p2Obn.FLOHtQZMGrxmv.9R32MGl5fmji2cz5Z9sGMBowZus9hbG'),(55,53,'Udupi','Tanish','9988556633','synced','$2a$10$dPqXQYdq6ISnrVZv.I/sl.v8e8HS7a7PoRBi1P4Iu75xLVxzW/OGK');
/*!40000 ALTER TABLE `patient_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_entity`
--

DROP TABLE IF EXISTS `user_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4xad1enskw4j1t2866f7sodrx` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_entity`
--

LOCK TABLES `user_entity` WRITE;
/*!40000 ALTER TABLE `user_entity` DISABLE KEYS */;
INSERT INTO `user_entity` VALUES (1,'admin@gmail.com',NULL,'$2a$10$MF7okemUHovSMHpli7J2KOo9u8ZbDg7cN4wbRdZokKrVjRh5yGyce','ADMIN'),(2,'doctorm@gmail.com','Manipal','$2a$10$X.GD5Rpfw4TaUGTOMIQDv.3SmfgnstRzCFxBeWgHphfO3q1caO/te','DOCTOR'),(3,'doctoru@gmail.com','Udupi','$2a$10$LMYxQ.ZA6chEk1tfycNGzuGlvcoJplTaSi2l3NhYqSA6MdcIBZMe2','DOCTOR'),(4,'doctork@gmail.com','Karkala','$2a$10$ufJ/jGLsmMtQD.M/..C5b.PoDQLWHOK6FbSFsLw5rchxZr0vZPtkG','DOCTOR'),(5,'doctorkun@gmail.com','Kundapur','$2a$10$K89lzGZNQj3okFRDzmwOXOojnCuTIqHlx72XCvWmXvs6BLzTUgv8K','DOCTOR');
/*!40000 ALTER TABLE `user_entity` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-26 18:11:10
