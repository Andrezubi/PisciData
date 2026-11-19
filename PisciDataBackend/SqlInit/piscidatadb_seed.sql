SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `aiconversation`
--

DROP TABLE IF EXISTS `aiconversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aiconversation` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `OwnerUserId` int NOT NULL,
  `StartDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LastMessageDate` datetime DEFAULT NULL,
  `Title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_AIConversation_User` (`UserId`),
  KEY `IX_AIConversation_OwnerUserId` (`OwnerUserId`),
  CONSTRAINT `FK_AIConversation_OwnerUser` FOREIGN KEY (`OwnerUserId`) REFERENCES `user` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_AIConversation_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aiconversation`
--

LOCK TABLES `aiconversation` WRITE;
/*!40000 ALTER TABLE `aiconversation` DISABLE KEYS */;
INSERT INTO `aiconversation` VALUES (1,1,'2026-10-27 19:05:48','2026-10-27 19:06:50','Conversacion Prueba','2026-10-27 15:05:48','2026-10-27 15:06:50',1,NULL);
/*!40000 ALTER TABLE `aiconversation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `aimessage`
--

DROP TABLE IF EXISTS `aimessage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aimessage` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `AIConversationId` int NOT NULL,
  `Role` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Content` text COLLATE utf8mb4_unicode_ci,
  `Transcription` text COLLATE utf8mb4_unicode_ci,
  `MessageType` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT',
  `MessageDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_AIMessage_User` (`UserId`),
  KEY `IX_AIMessage_AIConversationId` (`AIConversationId`),
  CONSTRAINT `FK_AIMessage_Conversation` FOREIGN KEY (`AIConversationId`) REFERENCES `aiconversation` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_AIMessage_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CK_AIMessage_Role` CHECK ((`Role` in (_utf8mb4'USER',_utf8mb4'ASSISTANT',_utf8mb4'SYSTEM',_utf8mb4'TOOL'))),
  CONSTRAINT `CK_AIMessage_Type` CHECK ((`MessageType` in (_utf8mb4'TEXT',_utf8mb4'VOICE',_utf8mb4'SYSTEM',_utf8mb4'TOOL_RESULT')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aimessage`
--

LOCK TABLES `aimessage` WRITE;
/*!40000 ALTER TABLE `aimessage` DISABLE KEYS */;
INSERT INTO `aimessage` VALUES (1,1,'user','quien eres?',NULL,'text','2026-10-27 19:06:50','2026-10-27 15:06:50',NULL,1,1),(2,1,'assistant','Soy el asistente inteligente **PisciData**, un sistema especializado en la gestión de piscigranjas y acuicultura. Mi objetivo es asistirle al productor con las mejores prácticas para la crianza, alimentación y cuidado de sus estanques.',NULL,'text','2026-10-27 19:06:50','2026-10-27 15:06:50',NULL,1,1);
/*!40000 ALTER TABLE `aimessage` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `biometrics`
--

DROP TABLE IF EXISTS `biometrics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biometrics` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `ProductionCycleId` int NOT NULL,
  `MeasurementDate` date NOT NULL,
  `AverageWeightGrams` decimal(10,2) DEFAULT NULL,
  `BiomassKg` decimal(12,3) DEFAULT NULL,
  `CalculatedFeedKg` decimal(12,3) DEFAULT NULL,
  `HealthStatus` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Observations` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Biometrics_User` (`UserId`),
  KEY `IX_Biometrics_ProductionCycleId` (`ProductionCycleId`),
  CONSTRAINT `FK_Biometrics_ProductionCycle` FOREIGN KEY (`ProductionCycleId`) REFERENCES `productioncycle` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Biometrics_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biometrics`
--

LOCK TABLES `biometrics` WRITE;
/*!40000 ALTER TABLE `biometrics` DISABLE KEYS */;
/*!40000 ALTER TABLE `biometrics` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `biometricssample`
--

DROP TABLE IF EXISTS `biometricssample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biometricssample` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `BiometricsId` int NOT NULL,
  `SampleNumber` int NOT NULL,
  `BucketWaterWeightKg` decimal(10,3) DEFAULT NULL,
  `BucketWithFishWeightKg` decimal(10,3) DEFAULT NULL,
  `FishCount` int NOT NULL,
  `TotalFishWeightKg` decimal(10,3) DEFAULT NULL,
  `AverageWeightGrams` decimal(10,2) DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_BiometricsSample_User` (`UserId`),
  KEY `IX_BiometricsSample_BiometricsId` (`BiometricsId`),
  CONSTRAINT `FK_BiometricsSample_Biometrics` FOREIGN KEY (`BiometricsId`) REFERENCES `biometrics` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_BiometricsSample_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biometricssample`
--

LOCK TABLES `biometricssample` WRITE;
/*!40000 ALTER TABLE `biometricssample` DISABLE KEYS */;
/*!40000 ALTER TABLE `biometricssample` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `farm`
--

DROP TABLE IF EXISTS `farm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farm` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `OwnerUserId` int NOT NULL,
  `Name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Latitude` decimal(10,7) DEFAULT NULL,
  `Longitude` decimal(10,7) DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `IX_Farm_OwnerUserId` (`OwnerUserId`),
  KEY `IX_Farm_UserId` (`UserId`),
  CONSTRAINT `FK_Farm_OwnerUser` FOREIGN KEY (`OwnerUserId`) REFERENCES `user` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Farm_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farm`
--

LOCK TABLES `farm` WRITE;
/*!40000 ALTER TABLE `farm` DISABLE KEYS */;
INSERT INTO `farm` VALUES (1,1,'Piscicola El Paraiso','Tambaqui and pacu grow-out farm with three earthen ponds.','Km 45 Carretera al Chapare, Villa Tunari, Cochabamba',-16.9742000,-65.4209000,'2026-10-13 16:48:37',NULL,1,1),(2,5,'Granja Los Tambaquies','Family-run farm focused on tambaqui production for local markets.','Comunidad Chimore, Carrasco, Cochabamba',-16.9906000,-65.1462000,'2026-10-13 16:48:37',NULL,1,5);
/*!40000 ALTER TABLE `farm` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `feed`
--

DROP TABLE IF EXISTS `feed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `FarmId` int NOT NULL,
  `Brand` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ProductName` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Phase` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ProteinPercentage` decimal(5,2) DEFAULT NULL,
  `PelletSizeMm` decimal(6,2) DEFAULT NULL,
  `StockKg` decimal(12,3) DEFAULT NULL,
  `MinimumStockKg` decimal(12,3) DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Feed_User` (`UserId`),
  KEY `IX_Feed_FarmId` (`FarmId`),
  CONSTRAINT `FK_Feed_Farm` FOREIGN KEY (`FarmId`) REFERENCES `farm` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Feed_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed`
--

LOCK TABLES `feed` WRITE;
/*!40000 ALTER TABLE `feed` DISABLE KEYS */;
/*!40000 ALTER TABLE `feed` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `feeding`
--

DROP TABLE IF EXISTS `feeding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feeding` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `ProductionCycleId` int NOT NULL,
  `FeedId` int NOT NULL,
  `FeedingDate` date NOT NULL,
  `FeedingTime` time NOT NULL,
  `QuantityKg` decimal(12,3) NOT NULL,
  `MealNumber` int DEFAULT NULL,
  `Behavior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Observations` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Feeding_User` (`UserId`),
  KEY `IX_Feeding_ProductionCycleId` (`ProductionCycleId`),
  KEY `IX_Feeding_FeedId` (`FeedId`),
  CONSTRAINT `FK_Feeding_Feed` FOREIGN KEY (`FeedId`) REFERENCES `feed` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Feeding_ProductionCycle` FOREIGN KEY (`ProductionCycleId`) REFERENCES `productioncycle` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Feeding_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feeding`
--

LOCK TABLES `feeding` WRITE;
/*!40000 ALTER TABLE `feeding` DISABLE KEYS */;
/*!40000 ALTER TABLE `feeding` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `feedingbyage`
--

DROP TABLE IF EXISTS `feedingbyage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedingbyage` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `SpeciesId` int DEFAULT NULL,
  `MinimumAgeDays` int NOT NULL,
  `MaximumAgeDays` int NOT NULL,
  `ApproximatedFishWeightGrams` decimal(7,2) DEFAULT NULL,
  `FeedingRatePercentage` decimal(6,3) DEFAULT NULL,
  `FeedPhase` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FeedForm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ProteinMinPercentage` decimal(5,2) DEFAULT NULL,
  `ProteinMaxPercentage` decimal(5,2) DEFAULT NULL,
  `PelletSizeMinMm` decimal(6,2) DEFAULT NULL,
  `PelletSizeMaxMm` decimal(6,2) DEFAULT NULL,
  `ReferenceFishCount` int NOT NULL DEFAULT '1000',
  `DailyAmountKilo` decimal(6,2) DEFAULT NULL,
  `DailyMeals` int DEFAULT NULL,
  `Source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_FeedingByAge_Species` (`SpeciesId`),
  KEY `FK_FeedingByAge_User` (`UserId`),
  CONSTRAINT `FK_FeedingByAge_Species` FOREIGN KEY (`SpeciesId`) REFERENCES `species` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_FeedingByAge_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CK_FeedingByAge_AgeRange` CHECK ((`MaximumAgeDays` >= `MinimumAgeDays`)),
  CONSTRAINT `CK_FeedingByAge_Form` CHECK (((`FeedForm` is null) or (`FeedForm` in (_utf8mb4'POWDER',_utf8mb4'PELLET',_utf8mb4'EXTRUDED')))),
  CONSTRAINT `CK_FeedingByAge_Pellet` CHECK (((`PelletSizeMaxMm` is null) or (`PelletSizeMinMm` is null) or (`PelletSizeMaxMm` >= `PelletSizeMinMm`))),
  CONSTRAINT `CK_FeedingByAge_Protein` CHECK (((`ProteinMaxPercentage` is null) or (`ProteinMinPercentage` is null) or (`ProteinMaxPercentage` >= `ProteinMinPercentage`))),
  CONSTRAINT `CK_FeedingByAge_ReferenceFish` CHECK ((`ReferenceFishCount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedingbyage`
--

LOCK TABLES `feedingbyage` WRITE;
/*!40000 ALTER TABLE `feedingbyage` DISABLE KEYS */;
INSERT INTO `feedingbyage` VALUES (1,1,0,6,0.50,NULL,NULL,'POWDER',55.00,55.00,NULL,NULL,1000,0.10,6,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(2,1,7,13,3.00,NULL,NULL,'POWDER',55.00,55.00,NULL,NULL,1000,0.50,6,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(3,1,14,20,7.00,NULL,NULL,'POWDER',55.00,55.00,NULL,NULL,1000,0.70,6,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(4,1,21,27,13.00,NULL,NULL,NULL,40.00,40.00,1.00,2.00,1000,1.00,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(5,1,28,34,20.00,NULL,NULL,NULL,40.00,40.00,1.00,2.00,1000,1.40,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(6,1,35,41,25.00,NULL,NULL,NULL,40.00,40.00,1.00,2.00,1000,1.60,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(7,1,42,48,32.00,NULL,NULL,NULL,40.00,40.00,2.00,4.00,1000,1.90,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(8,1,49,55,41.00,NULL,NULL,NULL,40.00,40.00,2.00,4.00,1000,2.10,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(9,1,56,62,54.00,NULL,NULL,NULL,40.00,40.00,2.00,4.00,1000,2.70,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(10,1,63,69,69.00,NULL,NULL,NULL,40.00,40.00,4.00,6.00,1000,3.20,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(11,1,70,76,85.00,NULL,NULL,NULL,32.00,32.00,4.00,6.00,1000,3.60,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(12,1,77,83,107.00,NULL,NULL,NULL,32.00,32.00,4.00,6.00,1000,4.00,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(13,1,84,90,132.00,NULL,NULL,NULL,32.00,32.00,4.00,6.00,1000,4.40,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(14,1,91,97,158.00,NULL,NULL,NULL,32.00,32.00,4.00,6.00,1000,4.70,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(15,1,98,104,188.00,NULL,NULL,NULL,32.00,32.00,4.00,6.00,1000,5.10,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(16,1,105,111,220.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,5.70,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(17,1,112,118,258.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,6.20,4,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(18,1,119,125,298.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,6.60,3,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(19,1,126,132,339.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,7.10,3,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(20,1,133,139,384.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,7.70,3,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(21,1,140,146,430.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,8.60,3,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(22,1,147,153,480.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,9.10,3,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(23,1,154,160,531.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,9.60,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(24,1,161,167,585.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,9.90,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(25,1,168,174,640.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,9.60,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(26,1,175,181,696.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(27,1,182,188,754.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,10.60,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(28,1,189,195,815.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,10.80,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(29,1,196,202,878.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,11.00,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(30,1,203,209,940.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,11.30,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL),(31,1,210,216,1000.00,NULL,NULL,NULL,28.00,28.00,8.00,8.00,1000,12.00,2,'Manual Pacú y Tambaquí 5ta ed., p.40','2026-10-13 16:48:37',NULL,1,NULL);
/*!40000 ALTER TABLE `feedingbyage` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `feedingbyweight`
--

DROP TABLE IF EXISTS `feedingbyweight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedingbyweight` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `SpeciesId` int DEFAULT NULL,
  `MinimumWeightGrams` decimal(10,2) NOT NULL,
  `MaximumWeightGrams` decimal(10,2) NOT NULL,
  `FeedingRatePercentage` decimal(6,3) DEFAULT NULL,
  `FeedPhase` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FeedForm` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ProteinMinPercentage` decimal(5,2) DEFAULT NULL,
  `ProteinMaxPercentage` decimal(5,2) DEFAULT NULL,
  `PelletSizeMinMm` decimal(6,2) DEFAULT NULL,
  `PelletSizeMaxMm` decimal(6,2) DEFAULT NULL,
  `DailyMeals` int DEFAULT NULL,
  `Source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_FeedingByWeight_Species` (`SpeciesId`),
  KEY `FK_FeedingByWeight_User` (`UserId`),
  CONSTRAINT `FK_FeedingByWeight_Species` FOREIGN KEY (`SpeciesId`) REFERENCES `species` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_FeedingByWeight_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CK_FeedingByWeight_Form` CHECK (((`FeedForm` is null) or (`FeedForm` in (_utf8mb4'POWDER',_utf8mb4'PELLET',_utf8mb4'EXTRUDED')))),
  CONSTRAINT `CK_FeedingByWeight_Pellet` CHECK (((`PelletSizeMaxMm` is null) or (`PelletSizeMinMm` is null) or (`PelletSizeMaxMm` >= `PelletSizeMinMm`))),
  CONSTRAINT `CK_FeedingByWeight_Protein` CHECK (((`ProteinMaxPercentage` is null) or (`ProteinMinPercentage` is null) or (`ProteinMaxPercentage` >= `ProteinMinPercentage`))),
  CONSTRAINT `CK_FeedingByWeight_WeightRange` CHECK ((`MaximumWeightGrams` > `MinimumWeightGrams`))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedingbyweight`
--

LOCK TABLES `feedingbyweight` WRITE;
/*!40000 ALTER TABLE `feedingbyweight` DISABLE KEYS */;
INSERT INTO `feedingbyweight` VALUES (1,1,0.00,5.00,15.000,'F0','POWDER',45.00,55.00,NULL,NULL,4,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(2,1,5.00,10.00,10.000,'F1','POWDER',40.00,40.00,NULL,NULL,3,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(3,1,10.00,50.00,7.000,'F1',NULL,40.00,40.00,2.00,4.00,3,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(4,1,50.00,100.00,5.000,'F1',NULL,40.00,40.00,2.00,4.00,3,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(5,1,100.00,200.00,4.000,'F2',NULL,30.00,30.00,4.00,6.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(6,1,200.00,300.00,3.000,'F2',NULL,30.00,30.00,4.00,6.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(7,1,300.00,400.00,2.500,'F2',NULL,30.00,30.00,4.00,6.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(8,1,400.00,500.00,2.000,'F3',NULL,28.00,28.00,6.00,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(9,1,500.00,600.00,1.500,'F3',NULL,28.00,28.00,6.00,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(10,1,600.00,700.00,1.200,'F3',NULL,28.00,28.00,6.00,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(11,1,700.00,800.00,1.000,'F3',NULL,28.00,28.00,6.00,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(12,1,800.00,1200.00,0.600,'F3',NULL,28.00,28.00,6.00,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL),(13,1,1200.00,2000.00,0.600,'F3',NULL,28.00,28.00,6.00,10.00,2,'Manual Pacú y Tambaquí 5ta ed., p.37','2026-10-13 16:48:37',NULL,1,NULL);
/*!40000 ALTER TABLE `feedingbyweight` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `harvest`
--

DROP TABLE IF EXISTS `harvest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `harvest` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `ProductionCycleId` int NOT NULL,
  `HarvestDate` date NOT NULL,
  `FishCount` int NOT NULL,
  `TotalWeightKg` decimal(12,3) DEFAULT NULL,
  `AverageWeightGrams` decimal(12,3) DEFAULT NULL,
  `HarvestType` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Observations` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Harvest_User` (`UserId`),
  KEY `IX_Harvest_ProductionCycleId` (`ProductionCycleId`),
  CONSTRAINT `FK_Harvest_ProductionCycle` FOREIGN KEY (`ProductionCycleId`) REFERENCES `productioncycle` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Harvest_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `harvest`
--

LOCK TABLES `harvest` WRITE;
/*!40000 ALTER TABLE `harvest` DISABLE KEYS */;
/*!40000 ALTER TABLE `harvest` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `mortality`
--

DROP TABLE IF EXISTS `mortality`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mortality` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `ProductionCycleId` int NOT NULL,
  `MortalityDate` date NOT NULL,
  `DeadFishCount` int NOT NULL,
  `Cause` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ObservedSigns` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Observations` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Mortality_User` (`UserId`),
  KEY `IX_Mortality_ProductionCycleId` (`ProductionCycleId`),
  CONSTRAINT `FK_Mortality_ProductionCycle` FOREIGN KEY (`ProductionCycleId`) REFERENCES `productioncycle` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Mortality_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mortality`
--

LOCK TABLES `mortality` WRITE;
/*!40000 ALTER TABLE `mortality` DISABLE KEYS */;
/*!40000 ALTER TABLE `mortality` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `pond`
--

DROP TABLE IF EXISTS `pond`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pond` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `FarmId` int NOT NULL,
  `Code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Shape` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Width` decimal(10,2) DEFAULT NULL,
  `Length` decimal(10,2) DEFAULT NULL,
  `Diameter` decimal(10,2) DEFAULT NULL,
  `Depth` decimal(10,2) DEFAULT NULL,
  `Area` decimal(12,2) DEFAULT NULL,
  `Description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UQ_Pond_Farm_Code` (`FarmId`,`Code`),
  KEY `IX_Pond_FarmId` (`FarmId`),
  KEY `IX_Pond_UserId` (`UserId`),
  CONSTRAINT `FK_Pond_Farm` FOREIGN KEY (`FarmId`) REFERENCES `farm` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Pond_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pond`
--

LOCK TABLES `pond` WRITE;
/*!40000 ALTER TABLE `pond` DISABLE KEYS */;
INSERT INTO `pond` VALUES (1,1,'P-01','Main Pond','Rectangular',20.00,30.00,NULL,1.50,600.00,'Largest pond, aerator installed.','2026-10-13 16:48:37',NULL,1,1),(2,1,'P-02','Nursery Pond','Rectangular',15.00,25.00,NULL,1.20,375.00,'Used for juveniles.','2026-10-13 16:48:37',NULL,1,1),(3,1,'P-03','Round Pond','Circular',NULL,NULL,12.00,1.40,113.10,'Geomembrane-lined circular tank.','2026-10-13 16:48:37',NULL,1,1),(4,2,'P-01','Pond A','Rectangular',20.00,20.00,NULL,1.60,400.00,'Supplied by gravity from the river channel.','2026-10-13 16:48:37',NULL,1,5),(5,2,'P-02','Pond B','Circular',NULL,NULL,10.00,1.30,78.54,'Small pond for sorting and quarantine.','2026-10-13 16:48:37',NULL,1,5);
/*!40000 ALTER TABLE `pond` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `productioncycle`
--

DROP TABLE IF EXISTS `productioncycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productioncycle` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `PondId` int NOT NULL,
  `SpeciesId` int NOT NULL,
  `StartDate` date NOT NULL,
  `EndDate` date DEFAULT NULL,
  `InitialFishCount` int DEFAULT NULL,
  `InitialAverageWeightGrams` decimal(7,2) DEFAULT NULL,
  `InitialAgeDays` int DEFAULT NULL,
  `Observations` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `IX_ProductionCycle_PondId` (`PondId`),
  KEY `IX_ProductionCycle_SpeciesId` (`SpeciesId`),
  KEY `IX_ProductionCycle_UserId` (`UserId`),
  CONSTRAINT `FK_ProductionCycle_Pond` FOREIGN KEY (`PondId`) REFERENCES `pond` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_ProductionCycle_Species` FOREIGN KEY (`SpeciesId`) REFERENCES `species` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_ProductionCycle_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productioncycle`
--

LOCK TABLES `productioncycle` WRITE;
/*!40000 ALTER TABLE `productioncycle` DISABLE KEYS */;
INSERT INTO `productioncycle` VALUES (1,1,1,'2026-03-02',NULL,5000,25.00,90,'Fingerlings from local hatchery. Cycle in progress.','2026-10-13 16:48:37',NULL,1,3),(2,2,1,'2026-08-15',NULL,3000,5.00,45,'Juveniles on high-protein feed.','2026-10-13 16:48:37',NULL,1,3),(3,3,1,'2025-09-01','2026-02-20',2000,10.00,60,'Completed cycle, harvested after 172 days.','2026-10-13 16:48:37',NULL,1,3),(4,4,1,'2026-06-10',NULL,4000,15.00,70,'Stocked at 10 fish per m2. Cycle in progress.','2026-10-13 16:48:37',NULL,1,5),(5,5,1,'2026-09-20',NULL,1000,3.00,35,'Small batch for restocking and testing.','2026-10-13 16:48:37',NULL,1,5);
/*!40000 ALTER TABLE `productioncycle` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `species`
--

DROP TABLE IF EXISTS `species`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `species` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `CommonName` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ScientificName` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UQ_Species_CommonName` (`CommonName`),
  KEY `FK_Species_User` (`UserId`),
  CONSTRAINT `FK_Species_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `species`
--

LOCK TABLES `species` WRITE;
/*!40000 ALTER TABLE `species` DISABLE KEYS */;
INSERT INTO `species` VALUES (1,'Tambaqui','Colossoma macropomum','Large Amazonian freshwater fish, also known as cachama or gamitana.','2026-10-13 16:48:37',NULL,1,1);
/*!40000 ALTER TABLE `species` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `supply`
--

DROP TABLE IF EXISTS `supply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supply` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `FarmId` int NOT NULL,
  `Name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Quantity` decimal(12,3) NOT NULL,
  `Unit` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `MinimumStock` decimal(12,3) DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Supply_User` (`UserId`),
  KEY `IX_Supply_FarmId` (`FarmId`),
  CONSTRAINT `FK_Supply_Farm` FOREIGN KEY (`FarmId`) REFERENCES `farm` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Supply_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supply`
--

LOCK TABLES `supply` WRITE;
/*!40000 ALTER TABLE `supply` DISABLE KEYS */;
INSERT INTO `supply` VALUES (1,1,'Agricultural lime','Water treatment',250.000,'kg',100.000,'2026-10-13 16:48:37',NULL,1,2),(2,1,'Common salt','Water treatment',120.000,'kg',50.000,'2026-10-13 16:48:37',NULL,1,2),(3,1,'Oxytetracycline','Medicine',3.000,'kg',1.000,'2026-10-13 16:48:37',NULL,1,3),(4,1,'Harvest net 50 m','Equipment',2.000,'units',1.000,'2026-10-13 16:48:37',NULL,1,2),(5,1,'Diesel for aerator pump','Fuel',80.000,'L',40.000,'2026-10-13 16:48:37',NULL,1,4),(6,1,'Plastic buckets 20 L','Equipment',6.000,'units',4.000,'2026-10-13 16:48:37',NULL,1,4),(7,2,'Agricultural lime','Water treatment',140.000,'kg',60.000,'2026-10-13 16:48:37',NULL,1,5),(8,2,'Common salt','Water treatment',35.000,'kg',40.000,'2026-10-13 16:48:37',NULL,1,5),(9,2,'Dissolved oxygen probe','Equipment',1.000,'units',1.000,'2026-10-13 16:48:37',NULL,1,5),(10,2,'Gasoline for water pump','Fuel',25.000,'L',20.000,'2026-10-13 16:48:37',NULL,1,5);
/*!40000 ALTER TABLE `supply` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `supplymovement`
--

DROP TABLE IF EXISTS `supplymovement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplymovement` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `SupplyId` int NOT NULL,
  `MovementDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MovementType` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Quantity` decimal(12,3) NOT NULL,
  `Reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_SupplyMovement_User` (`UserId`),
  KEY `IX_SupplyMovement_SupplyId` (`SupplyId`),
  CONSTRAINT `FK_SupplyMovement_Supply` FOREIGN KEY (`SupplyId`) REFERENCES `supply` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_SupplyMovement_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CK_SupplyMovement_Type` CHECK ((`MovementType` in (_utf8mb4'IN',_utf8mb4'OUT',_utf8mb4'ADJUSTMENT')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplymovement`
--

LOCK TABLES `supplymovement` WRITE;
/*!40000 ALTER TABLE `supplymovement` DISABLE KEYS */;
/*!40000 ALTER TABLE `supplymovement` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `FarmId` int NOT NULL,
  `PondId` int DEFAULT NULL,
  `Title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ScheduledDate` datetime DEFAULT NULL,
  `CompletedDate` datetime DEFAULT NULL,
  `Priority` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Normal',
  `Completed` tinyint(1) NOT NULL DEFAULT '0',
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_Task_User` (`UserId`),
  KEY `IX_Task_FarmId` (`FarmId`),
  KEY `IX_Task_PondId` (`PondId`),
  CONSTRAINT `FK_Task_Farm` FOREIGN KEY (`FarmId`) REFERENCES `farm` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_Task_Pond` FOREIGN KEY (`PondId`) REFERENCES `pond` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_Task_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,1,1,'Biometric sampling P-01','Take 3 samples of 30 fish each and record average weight.','2026-10-10 08:00:00',NULL,'High',0,'2026-10-13 16:48:37',NULL,1,2),(2,1,1,'Check aerator','Inspect motor and paddles, change oil if needed.','2026-10-09 07:00:00',NULL,'Urgent',0,'2026-10-13 16:48:37',NULL,1,2),(3,1,2,'Apply agricultural lime','Apply 10 kg of lime along the pond edges.','2026-10-12 09:00:00',NULL,'Normal',0,'2026-10-13 16:48:37',NULL,1,3),(4,1,NULL,'Order feed for next month','Request quotes from two suppliers and place the order.','2026-10-15 10:00:00',NULL,'Normal',0,'2026-10-13 16:48:37',NULL,1,2),(5,1,3,'Clean and dry pond P-03','Pond is empty after harvest. Clean liner and let it dry.','2026-02-25 08:00:00','2026-02-27 16:30:00','Low',1,'2026-10-13 16:48:37',NULL,1,4),(6,1,NULL,'Refill diesel for pump','Buy 40 L of diesel and refill the tank.','2026-10-04 09:00:00','2026-10-04 11:20:00','Normal',1,'2026-10-13 16:48:37',NULL,1,4),(7,2,4,'Treat algae bloom in Pond A','pH reached 8.3. Reduce feeding, increase water exchange, retest.','2026-10-08 15:00:00',NULL,'Urgent',0,'2026-10-13 16:48:37',NULL,1,5),(8,2,5,'Weekly weight check','Weigh 20 juveniles and update the feeding table.','2026-10-13 08:00:00',NULL,'Normal',0,'2026-10-13 16:48:37',NULL,1,5),(9,2,NULL,'Buy salt','Stock is below minimum. Buy at least 50 kg.','2026-10-11 10:00:00',NULL,'High',0,'2026-10-13 16:48:37',NULL,1,5),(10,2,4,'Calibrate oxygen probe','Calibrate the probe with the standard solution.','2026-10-01 07:30:00','2026-10-01 08:10:00','Low',1,'2026-10-13 16:48:37',NULL,1,5);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `LastName` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Phone` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `PasswordHash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Role` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Owner',
  `LastLoginAt` datetime DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_User_User` (`UserId`),
  CONSTRAINT `FK_User_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Carlos','Mendoza','70123456','$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi','Owner','2026-10-27 20:16:46','2026-10-13 16:48:37','2026-10-27 16:16:45',1,1),(2,'Lucia','Rojas','71234567','$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi','Administrator','2026-10-08 07:45:00','2026-10-13 16:48:37','2026-10-13 16:48:37',1,1),(3,'Miguel','Flores','72345678','$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi','Technician','2026-10-08 06:50:00','2026-10-13 16:48:37','2026-10-13 16:48:37',1,1),(4,'Juan','Quispe','73456789','$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi','Worker','2026-10-08 06:10:00','2026-10-13 16:48:37','2026-10-13 16:48:37',1,1),(5,'Ana','Vargas','74567890','$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi','Owner','2026-10-06 20:15:00','2026-10-13 16:48:37','2026-10-13 16:48:37',1,5);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `waterquality`
--

DROP TABLE IF EXISTS `waterquality`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `waterquality` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `ProductionCycleId` int NOT NULL,
  `MeasurementDate` date NOT NULL,
  `MeasurementTime` time NOT NULL,
  `TransparencyCm` decimal(8,2) DEFAULT NULL,
  `TemperatureC` decimal(6,2) DEFAULT NULL,
  `PH` decimal(5,2) DEFAULT NULL,
  `DissolvedOxygen` decimal(8,3) DEFAULT NULL,
  `Observations` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_WaterQuality_User` (`UserId`),
  KEY `IX_WaterQuality_ProductionCycleId` (`ProductionCycleId`),
  CONSTRAINT `FK_WaterQuality_ProductionCycle` FOREIGN KEY (`ProductionCycleId`) REFERENCES `productioncycle` (`Id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_WaterQuality_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `waterquality`
--

LOCK TABLES `waterquality` WRITE;
/*!40000 ALTER TABLE `waterquality` DISABLE KEYS */;
INSERT INTO `waterquality` VALUES (1,1,'2026-10-05','06:00:00',35.00,25.80,7.10,4.200,'Normal early-morning values.','2026-10-13 16:48:37',NULL,1,4),(2,1,'2026-10-05','16:00:00',33.00,28.90,7.60,7.100,NULL,'2026-10-13 16:48:37',NULL,1,4),(3,1,'2026-10-06','06:00:00',34.00,25.60,7.00,3.900,'Aerator turned on at 05:00.','2026-10-13 16:48:37',NULL,1,4),(4,1,'2026-10-07','06:00:00',30.00,26.10,6.90,3.400,'Slightly low oxygen, extended aeration.','2026-10-13 16:48:37',NULL,1,4),(5,1,'2026-10-07','16:00:00',28.00,29.30,7.50,6.800,NULL,'2026-10-13 16:48:37',NULL,1,4),(6,2,'2026-10-06','06:30:00',40.00,26.40,7.30,5.100,'Clear water, good plankton bloom.','2026-10-13 16:48:37',NULL,1,3),(7,2,'2026-10-07','06:30:00',38.00,26.20,7.20,4.800,NULL,'2026-10-13 16:48:37',NULL,1,3),(8,2,'2026-10-08','06:30:00',37.00,26.50,7.20,4.700,NULL,'2026-10-13 16:48:37',NULL,1,3),(9,4,'2026-10-06','07:00:00',32.00,27.00,7.40,4.500,NULL,'2026-10-13 16:48:37',NULL,1,5),(10,4,'2026-10-07','07:00:00',31.00,27.30,7.50,4.300,NULL,'2026-10-13 16:48:37',NULL,1,5),(11,4,'2026-10-08','07:00:00',25.00,27.80,8.30,3.100,'High pH and low oxygen, possible algae bloom.','2026-10-13 16:48:37',NULL,1,5),(12,5,'2026-10-07','07:30:00',45.00,26.90,7.00,5.400,'New batch adapting well.','2026-10-13 16:48:37',NULL,1,5),(13,5,'2026-10-08','07:30:00',44.00,27.10,7.10,5.200,NULL,'2026-10-13 16:48:37',NULL,1,5);
/*!40000 ALTER TABLE `waterquality` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:53

-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: piscidatadb
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `waterqualityreference`
--

DROP TABLE IF EXISTS `waterqualityreference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `waterqualityreference` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `SpeciesId` int DEFAULT NULL,
  `Parameter` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Unit` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `OptimalMin` decimal(8,2) DEFAULT NULL,
  `OptimalMax` decimal(8,2) DEFAULT NULL,
  `WarningMin` decimal(8,2) DEFAULT NULL,
  `WarningMax` decimal(8,2) DEFAULT NULL,
  `CriticalMin` decimal(8,2) DEFAULT NULL,
  `CriticalMax` decimal(8,2) DEFAULT NULL,
  `RecommendedAction` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `UserId` int DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_WaterQualityReference_Species` (`SpeciesId`),
  KEY `FK_WaterQualityReference_User` (`UserId`),
  KEY `IX_WaterQualityReference_Parameter` (`Parameter`,`SpeciesId`),
  CONSTRAINT `FK_WaterQualityReference_Species` FOREIGN KEY (`SpeciesId`) REFERENCES `species` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_WaterQualityReference_User` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CK_WaterQualityReference_Optimal` CHECK (((`OptimalMin` is null) or (`OptimalMax` is null) or (`OptimalMax` >= `OptimalMin`))),
  CONSTRAINT `CK_WaterQualityReference_Parameter` CHECK ((`Parameter` in (_utf8mb4'DISSOLVED_OXYGEN',_utf8mb4'PH',_utf8mb4'TEMPERATURE',_utf8mb4'TRANSPARENCY')))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `waterqualityreference`
--

LOCK TABLES `waterqualityreference` WRITE;
/*!40000 ALTER TABLE `waterqualityreference` DISABLE KEYS */;
INSERT INTO `waterqualityreference` VALUES (1,1,'DISSOLVED_OXYGEN','mg/L',4.00,NULL,2.00,NULL,1.00,NULL,'Stop feeding and fertilizing. Exchange at least 25% of the water (drop it from 1 m height). Pump surface water, reduce fish numbers without entering the pond, apply probiotics, install aerators.','Manual Pacú y Tambaquí 5ta ed., p.20-22, 27','2026-10-13 16:48:37',NULL,1,NULL),(2,1,'PH','pH',7.00,8.50,5.00,10.00,4.00,11.00,'If low, apply agricultural lime according to the dose table (p.17). Measure at about 06:00 and 18:00; the difference between both readings must not exceed 2 units.','Manual Pacú y Tambaquí 5ta ed., p.24, 27','2026-10-13 16:48:37',NULL,1,NULL),(3,1,'TEMPERATURE','C',25.00,32.00,20.00,37.00,16.00,38.00,'Reduce or suspend feeding until the temperature stabilizes. Avoid handling the fish.','Manual Pacú y Tambaquí 5ta ed., p.24','2026-10-13 16:48:37',NULL,1,NULL),(4,1,'TRANSPARENCY','cm',25.00,40.00,NULL,NULL,25.00,NULL,'Below 25 cm: do not fertilize, do not feed, exchange about 25% of the water. Above 40 cm: fertilize.','Manual Pacú y Tambaquí 5ta ed., p.23, 27','2026-10-13 16:48:37',NULL,1,NULL);
/*!40000 ALTER TABLE `waterqualityreference` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27 16:26:54

SET FOREIGN_KEY_CHECKS=1;
