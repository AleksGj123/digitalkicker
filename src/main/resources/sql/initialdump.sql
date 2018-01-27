-- MySQL dump 10.13  Distrib 5.7.18, for Linux (x86_64)
--
-- Host: localhost    Database: kickerstats
-- ------------------------------------------------------
-- Server version	5.7.18-0ubuntu0.16.10.1

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
-- Table structure for table `Matches`
--

DROP TABLE IF EXISTS `Matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Matches` (
  `id` bigint(20) NOT NULL,
  `goalsTeam1` int(11) NOT NULL,
  `goalsTeam2` int(11) NOT NULL,
  `matchtype` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `keeperTeam1_id` bigint(20) NOT NULL,
  `keeperTeam2_id` bigint(20) NOT NULL,
  `season_id` bigint(20) NOT NULL,
  `strikerTeam1_id` bigint(20) DEFAULT NULL,
  `strikerTeam2_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK23kavt9c6fev9ukds7xa4mcy0` (`keeperTeam1_id`),
  KEY `FKeei5mqhqn4d0fuwq2q6ijx6jv` (`keeperTeam2_id`),
  KEY `FKnjmpvgsaxg8d8benx181l6kks` (`season_id`),
  KEY `FKjh4ku6dxqyimi5y28mbb3bqlo` (`strikerTeam1_id`),
  KEY `FKn7iy08u45fdceeh4fm202x06s` (`strikerTeam2_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Matches`
--

LOCK TABLES `Matches` WRITE;
/*!40000 ALTER TABLE `Matches` DISABLE KEYS */;
/*!40000 ALTER TABLE `Matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player`
--

DROP TABLE IF EXISTS `Player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player` (
  `id` bigint(20) NOT NULL,
  `biography` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `forename` varchar(255) DEFAULT NULL,
  `lokSafe` bit(1) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `passwordHash` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player`
--

LOCK TABLES `Player` WRITE;
/*!40000 ALTER TABLE `Player` DISABLE KEYS */;
INSERT INTO `Player` VALUES (1,'.....','aleksandar.gjurcinov@bechtle.com','Aleks','\0','The Sheriff','$2a$10$bBVWmyqbWFKxryYfLoHKuu4FrH9yNIUJRPUXATIb280XFGraaioWK','Gjurcinov'),(2,'...','Rouven.Pfeiffer@bechtle.com','Rouven','\0','The Hornet','$2a$10$VzVWEc3nW6WLpuA.OfBP/easl2LzbwvQMr9VHvbUqA8s0wC5.ZCTu','Pfeiffer'),(3,'...','Enrico.Mischorr@bechtle.com','Enrico','\0','The Wall','$2a$10$ffW9gxr2dJC.y7Iec/UzhuV7Z3Zcn6OoTAijZyU5iBNlEF1muHMQO','Mischorr'),(4,'...','christopher.klein@bechtle.com','Chris','\0','The Arrrrogant Pirat','$2a$10$h3FpGZF3rBTf5HXnvhANJ.1zG1GJ9jDD4kWEfqsTkk5M9BP2.pygS','Klein'),(5,'...','Bastian.Reiss@Bechtle.com','Basti','\0','???','$2a$10$j9A58FhOootJuSvGdcImi.XTBBhYFLsL1lXv4vMfNYKx12dOO/iSa','Reiss'),(6,'...','andreas.nagy@bechtle.com','Andy','\0','Fluffy','$2a$10$kBbvypMOk6MI3VJPC.vHIeiUNn2x50bxzqqE7DKjoi68LsWMuX6ie','Nagy'),(7,'...','maik.lieweries@bechtle.com','Maik','\0','???','$2a$10$Z1e6Cun8jcg/aKF5daKbM.eoOUKTVZ3HnbEzXVqcb6Q7SOL8y9GV6','Lieweris'),(8,'...','andrej.gedert@bechtle.com','Andrej','\0','Dampfhammer','$2a$10$LVoumsYcl319..6lW7aE8emklkrHTHOd852cutZ1eFokABUTHwLqW','Gedert'),(9,'...','hendrik.strenger@bechtle.com','Hendrik','\0','???','$2a$10$zav06WEz8MIpnOPoZ3z4Xe.MTmp6NCtxuajjnFyKWVKgKw4.nV9ei','Strenger'),(10,'...','simon.federau@bechtle.com','Simon','\0','Seuchenvogel','$2a$10$PyxgL.d3SnCtQpmaCHdW7uEhPwEXyJydTEK7iV5YWBBit2oR7jcFm','Federau'),(11,'???','marcus.ehret@bechtle.com','Marcus','\0','???','$2a$10$VCprSWhGUGORj06nYIWU4.yChAz12MvGMjv4Jv4umNa9HEuUMw/v.','Ehret'),(12,'???','ali.erdem@bechtle.com','Ali','\0','Baba','$2a$10$3gwnrs8yKgpPOPNzOBEP8O1e7v6RGBZfu7c6C2V42LqiMogsX7VrK','Erdem'),(14,'???','denis.mylnikov@bechtle.com','Denis','\0','???','$2a$10$1MiG0pFnVMmVW3zk7Cdh4.nLsqnbHyUQ0311l6YyUsMP06Vy6wOhC','Mylnikov');
/*!40000 ALTER TABLE `Player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player_Matches`
--

DROP TABLE IF EXISTS `Player_Matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player_Matches` (
  `Player_id` bigint(20) NOT NULL,
  `matches_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_8w6kcu9xt3886f41d6krjl7cg` (`matches_id`),
  KEY `FK3g07pq92b54bpkec00nrtpim0` (`Player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player_Matches`
--

LOCK TABLES `Player_Matches` WRITE;
/*!40000 ALTER TABLE `Player_Matches` DISABLE KEYS */;
/*!40000 ALTER TABLE `Player_Matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Season`
--

DROP TABLE IF EXISTS `Season`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Season` (
  `id` bigint(20) NOT NULL,
  `endDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `startDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Season`
--

LOCK TABLES `Season` WRITE;
/*!40000 ALTER TABLE `Season` DISABLE KEYS */;
INSERT INTO `Season` VALUES (13,'2018-12-21 00:00:00','Season 2018','2018-01-02 00:00:00');
/*!40000 ALTER TABLE `Season` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Season_Matches`
--

DROP TABLE IF EXISTS `Season_Matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Season_Matches` (
  `Season_id` bigint(20) NOT NULL,
  `matches_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_56g736chpqx17b77bfyvjomyh` (`matches_id`),
  KEY `FKgb5qpk6bg8di5ne613pyie938` (`Season_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Season_Matches`
--

LOCK TABLES `Season_Matches` WRITE;
/*!40000 ALTER TABLE `Season_Matches` DISABLE KEYS */;
/*!40000 ALTER TABLE `Season_Matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (15),(15),(15);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-01-05 22:18:14
