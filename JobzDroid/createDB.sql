-- MySQL dump 10.13  Distrib 5.1.51, for Win64 (unknown)
--
-- Host: localhost    Database: dbjobzdroid
-- ------------------------------------------------------
-- Server version	5.1.51-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `dbjobzdroid`
--

DROP DATABASE IF EXISTS dbjobzdroid;
CREATE DATABASE IF NOT EXISTS `dbjobzdroid` DEFAULT CHARACTER SET utf8;

USE `dbjobzdroid`;

--
-- Table structure for table `tableAccount`
--

DROP TABLE IF EXISTS `tableAccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableAccount` (
  `idAccount` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(254) NOT NULL,
  `password` varchar(45) NOT NULL,
  `secondaryEmail` varchar(254) DEFAULT NULL,
  `type` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL,
  `dateTimeCreated` bigint(20) NOT NULL,
  PRIMARY KEY (`idAccount`),
  UNIQUE KEY `idtableAccount_UNIQUE` (`idAccount`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10069 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableAccount`
--

LOCK TABLES `tableAccount` WRITE;
/*!40000 ALTER TABLE `tableAccount` DISABLE KEYS */;
INSERT INTO `tableAccount` VALUES (1,'superAdmin','17c4520f6cfd1ab53d8745e84681eb49',NULL,'superAdmin','active',1299717608561);
/*!40000 ALTER TABLE `tableAccount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableCandidate`
--

DROP TABLE IF EXISTS `tableCandidate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableCandidate` (
  `idAccount` int(11) NOT NULL DEFAULT '0',
  `idSearcher` int(11) NOT NULL DEFAULT '0',
  `dateAdded` mediumtext,
  PRIMARY KEY (`idAccount`,`idSearcher`),
  KEY `idSearcher` (`idSearcher`),
  CONSTRAINT `tableCandidate_ibfk_1` FOREIGN KEY (`idSearcher`) REFERENCES `tableprofilesearcher` (`idAccount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableCandidate`
--

LOCK TABLES `tableCandidate` WRITE;
/*!40000 ALTER TABLE `tableCandidate` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableCandidate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableEmailVerification`
--

DROP TABLE IF EXISTS `tableEmailVerification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableEmailVerification` (
  `idEmailVerification` varchar(36) NOT NULL,
  `idAccount` int(11) NOT NULL,
  `expiryTime` bigint(20) NOT NULL,
  `emailPending` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`idEmailVerification`),
  UNIQUE KEY `idtableEmailVerification_UNIQUE` (`idEmailVerification`),
  KEY `idAccount` (`idAccount`),
  CONSTRAINT `idAccount` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableEmailVerification`
--

LOCK TABLES `tableEmailVerification` WRITE;
/*!40000 ALTER TABLE `tableEmailVerification` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableEmailVerification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableEmpTypeJobAd`
--

DROP TABLE IF EXISTS `tableEmpTypeJobAd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableEmpTypeJobAd` (
  `idAccount` int(11) NOT NULL DEFAULT '0',
  `idJobAd` int(11) NOT NULL DEFAULT '0',
  `dateAdded` mediumtext,
  PRIMARY KEY (`idAccount`,`idJobAd`),
  KEY `idJobAd` (`idJobAd`),
  CONSTRAINT `tableEmpTypeJobAd_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`),
  CONSTRAINT `tableEmpTypeJobAd_ibfk_2` FOREIGN KEY (`idJobAd`) REFERENCES `tablejobad` (`idJobAd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableEmpTypeJobAd`
--

LOCK TABLES `tableEmpTypeJobAd` WRITE;
/*!40000 ALTER TABLE `tableEmpTypeJobAd` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableEmpTypeJobAd` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableJobAd`
--

DROP TABLE IF EXISTS `tableJobAd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableJobAd` (
  `idJobAd` int(11) NOT NULL AUTO_INCREMENT,
  `idAccount` int(11) DEFAULT NULL,
  `title` varchar(150) DEFAULT NULL,
  `location` varchar(90) DEFAULT NULL,
  `description` longtext,
  `expiryDate` mediumtext,
  `dateStarting` mediumtext,
  `datePosted` mediumtext,
  `status` varchar(45) DEFAULT NULL,
  `contactInfo` varchar(80) DEFAULT NULL,
  `educationRequired` varchar(45) DEFAULT '0',
  `jobAvailability` varchar(20) DEFAULT NULL,
  `tags` varchar(45) DEFAULT NULL,
  `numberOfViews` int(11) DEFAULT '0',
  `isApproved` int(11) DEFAULT '0',
  `hasGradFunding` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`idJobAd`),
  UNIQUE KEY `idJobAd_UNIQUE` (`idJobAd`),
  KEY `tableJobAd_ibfk_1` (`idAccount`),
  CONSTRAINT `tableJobAd_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableJobAd`
--

LOCK TABLES `tableJobAd` WRITE;
/*!40000 ALTER TABLE `tableJobAd` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableJobAd` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableLocationJobAd`
--

DROP TABLE IF EXISTS `tableLocationJobAd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableLocationJobAd` (
  `idJobAd` int(11) NOT NULL,
  `location` mediumtext,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `addr0` mediumtext,
  `latlng0` longtext,
  `addr1` mediumtext,
  `latlng1` longtext,
  `addr2` mediumtext,
  `latlng2` longtext,
  PRIMARY KEY (`idJobAd`),
  CONSTRAINT `tableLocationJobAd_ibfk_1` FOREIGN KEY (`idJobAd`) REFERENCES `tablejobad` (`idJobAd`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableLocationJobAd`
--

LOCK TABLES `tableLocationJobAd` WRITE;
/*!40000 ALTER TABLE `tableLocationJobAd` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableLocationJobAd` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableLocationProfile`
--

DROP TABLE IF EXISTS `tableLocationProfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableLocationProfile` (
  `idAccount` int(11) NOT NULL,
  `location` varchar(150) NOT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  PRIMARY KEY (`idAccount`,`location`),
  CONSTRAINT `tableLocationProfile_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableLocationProfile`
--

LOCK TABLES `tableLocationProfile` WRITE;
/*!40000 ALTER TABLE `tableLocationProfile` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableLocationProfile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableNews`
--

DROP TABLE IF EXISTS `tableNews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableNews` (
  `idNews` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `content` varchar(11000) DEFAULT '',
  `dateTimePublished` bigint(20) NOT NULL,
  PRIMARY KEY (`idNews`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableNews`
--

LOCK TABLES `tableNews` WRITE;
/*!40000 ALTER TABLE `tableNews` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableNews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tablePasswordReset`
--

DROP TABLE IF EXISTS `tablePasswordReset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tablePasswordReset` (
  `idPasswordReset` varchar(36) NOT NULL,
  `idAccount` int(11) NOT NULL DEFAULT '0',
  `expiryTime` bigint(20) NOT NULL,
  PRIMARY KEY (`idPasswordReset`),
  UNIQUE KEY `idtablePasswordReset_UNIQUE` (`idPasswordReset`),
  KEY `idAccount` (`idAccount`),
  CONSTRAINT `tablePasswordReset_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tablePasswordReset`
--

LOCK TABLES `tablePasswordReset` WRITE;
/*!40000 ALTER TABLE `tablePasswordReset` DISABLE KEYS */;
/*!40000 ALTER TABLE `tablePasswordReset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableProfilePoster`
--

DROP TABLE IF EXISTS `tableProfilePoster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableProfilePoster` (
  `idAccount` int(11) NOT NULL,
  `name` varchar(45) NOT NULL DEFAULT '',
  `phone` varchar(45) DEFAULT '',
  `selfDescription` longtext,
  PRIMARY KEY (`idAccount`),
  UNIQUE KEY `idAccount_UNIQUE` (`idAccount`),
  CONSTRAINT `tableProfilePoster_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableProfilePoster`
--

LOCK TABLES `tableProfilePoster` WRITE;
/*!40000 ALTER TABLE `tableProfilePoster` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableProfilePoster` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableSearcherEmpPref`
--

DROP TABLE IF EXISTS `tableSearcherEmpPref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableSearcherEmpPref` (
  `idAccount` int(11) NOT NULL,
  `fullTime` tinyint(1) DEFAULT '0',
  `partTime` tinyint(1) DEFAULT '0',
  `internship` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`idAccount`),
  CONSTRAINT `tableSearcherEmpPref_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableSearcherEmpPref`
--

LOCK TABLES `tableSearcherEmpPref` WRITE;
/*!40000 ALTER TABLE `tableSearcherEmpPref` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableSearcherEmpPref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableSession`
--

DROP TABLE IF EXISTS `tableSession`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableSession` (
  `sessionKey` varchar(36) NOT NULL,
  `idAccount` int(11) NOT NULL,
  `expiryTime` bigint(20) NOT NULL,
  PRIMARY KEY (`sessionKey`),
  UNIQUE KEY `sessionKey_UNIQUE` (`sessionKey`),
  KEY `idAccount` (`idAccount`),
  CONSTRAINT `tableSession_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableSession`
--

LOCK TABLES `tableSession` WRITE;
/*!40000 ALTER TABLE `tableSession` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableSession` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableTagMap`
--

DROP TABLE IF EXISTS `tableTagMap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableTagMap` (
  `idJobAd` int(11) NOT NULL,
  `idTags` int(11) NOT NULL,
  PRIMARY KEY (`idJobAd`,`idTags`),
  KEY `idJobAd` (`idJobAd`),
  KEY `idTags` (`idTags`),
  CONSTRAINT `tableTagMap_ibfk_1` FOREIGN KEY (`idJobAd`) REFERENCES `tablejobad` (`idJobAd`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tableTagMap_ibfk_2` FOREIGN KEY (`idTags`) REFERENCES `tabletags` (`idTags`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableTagMap`
--

LOCK TABLES `tableTagMap` WRITE;
/*!40000 ALTER TABLE `tableTagMap` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableTagMap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableTags`
--

DROP TABLE IF EXISTS `tableTags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableTags` (
  `idTags` int(11) NOT NULL,
  `tagName` varchar(45) NOT NULL,
  PRIMARY KEY (`idTags`),
  UNIQUE KEY `idtableTags_UNIQUE` (`idTags`),
  UNIQUE KEY `tagName_UNIQUE` (`tagName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableTags`
--

LOCK TABLES `tableTags` WRITE;
/*!40000 ALTER TABLE `tableTags` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableTags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tablefavouritejobad`
--

DROP TABLE IF EXISTS `tablefavouritejobad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tablefavouritejobad` (
  `idAccount` int(11) NOT NULL DEFAULT '0',
  `idJobAd` int(11) NOT NULL DEFAULT '0',
  `dateAdded` mediumtext,
  PRIMARY KEY (`idAccount`,`idJobAd`),
  KEY `idJobAd` (`idJobAd`),
  CONSTRAINT `tablefavouritejobad_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`),
  CONSTRAINT `tablefavouritejobad_ibfk_2` FOREIGN KEY (`idJobAd`) REFERENCES `tablejobad` (`idJobAd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tablefavouritejobad`
--

LOCK TABLES `tablefavouritejobad` WRITE;
/*!40000 ALTER TABLE `tablefavouritejobad` DISABLE KEYS */;
/*!40000 ALTER TABLE `tablefavouritejobad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tableprofilesearcher`
--

DROP TABLE IF EXISTS `tableprofilesearcher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tableprofilesearcher` (
  `idAccount` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `selfDescription` longtext,
  `educationLevel` int(11) DEFAULT '0',
  `startingDate` bigint(20) DEFAULT '0',
  PRIMARY KEY (`idAccount`),
  UNIQUE KEY `idAccount_UNIQUE` (`idAccount`),
  KEY `idAccount` (`idAccount`),
  CONSTRAINT `tableProfileSearcher_ibfk_1` FOREIGN KEY (`idAccount`) REFERENCES `tableaccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tableprofilesearcher`
--

LOCK TABLES `tableprofilesearcher` WRITE;
/*!40000 ALTER TABLE `tableprofilesearcher` DISABLE KEYS */;
/*!40000 ALTER TABLE `tableprofilesearcher` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-04-08  8:22:42