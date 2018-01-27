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
-- Current Database: `kickerstats`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kickerstats` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `kickerstats`;

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
  `loksafePlayer_id` bigint(20) DEFAULT NULL,
  `season_id` bigint(20) NOT NULL,
  `strikerTeam1_id` bigint(20) DEFAULT NULL,
  `strikerTeam2_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK23kavt9c6fev9ukds7xa4mcy0` (`keeperTeam1_id`),
  KEY `FKeei5mqhqn4d0fuwq2q6ijx6jv` (`keeperTeam2_id`),
  KEY `FKs7w0767iphojw7pi26np0nkj8` (`loksafePlayer_id`),
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
INSERT INTO `Matches` VALUES (74,2,5,'R',1,'2018-01-07 19:57:51',1,55,NULL,13,2,54),(75,2,5,'R',1,'2018-01-07 19:58:42',14,9,50,13,7,50),(76,4,5,'R',1,'2018-01-07 19:59:10',55,1,NULL,13,2,54),(77,2,5,'R',1,'2018-01-07 19:59:31',14,50,50,13,7,9),(78,4,5,'R',1,'2018-01-07 20:00:11',1,54,NULL,13,55,2),(79,0,5,'R',1,'2018-01-07 20:00:28',9,2,NULL,13,14,54),(81,3,5,'R',1,'2018-01-07 20:01:38',55,54,NULL,13,8,2),(82,3,5,'R',1,'2018-01-07 20:01:57',50,14,50,13,9,2),(84,0,1,'D',1,'2018-01-07 20:02:29',8,54,NULL,13,NULL,NULL),(85,0,5,'R',1,'2018-01-07 20:17:22',8,55,NULL,13,54,2),(86,0,1,'D',1,'2018-01-07 20:17:27',8,54,NULL,13,NULL,NULL),(87,5,2,'R',1,'2018-01-07 20:18:01',9,14,50,13,2,50),(88,1,5,'R',1,'2018-01-07 20:18:42',9,8,NULL,13,55,54),(89,4,5,'R',1,'2018-01-07 20:19:12',1,14,NULL,13,2,54),(90,5,1,'R',1,'2018-01-07 20:19:52',6,1,NULL,13,2,8),(91,1,5,'R',1,'2018-01-07 20:20:16',50,4,50,13,9,54),(92,5,4,'R',1,'2018-01-07 20:20:42',1,2,NULL,13,6,55),(93,5,2,'R',1,'2018-01-07 20:21:26',9,14,50,13,50,4),(94,5,4,'R',1,'2018-01-07 20:21:57',55,7,NULL,13,1,8),(95,1,5,'R',1,'2018-01-07 20:22:27',6,4,NULL,13,54,2),(96,0,5,'R',1,'2018-01-07 20:22:58',14,8,50,13,50,9),(98,5,2,'R',1,'2018-01-07 20:25:09',2,1,NULL,13,54,55),(99,5,4,'R',1,'2018-01-07 20:25:40',9,14,NULL,13,8,4),(100,3,5,'R',1,'2018-01-07 20:26:09',54,1,NULL,13,55,2),(101,5,1,'R',1,'2018-01-07 20:26:42',8,14,NULL,13,4,1),(102,5,4,'R',1,'2018-01-07 20:27:16',4,54,NULL,13,55,2),(103,5,4,'R',1,'2018-01-07 20:27:46',1,55,NULL,13,6,4),(104,5,3,'R',1,'2018-01-07 20:28:23',8,4,NULL,13,1,55),(105,4,5,'R',1,'2018-01-07 20:29:00',1,4,NULL,13,8,55),(106,5,3,'R',1,'2018-01-07 20:29:35',8,4,NULL,13,55,2),(107,3,5,'R',1,'2018-01-07 20:29:58',8,4,NULL,13,55,2),(108,0,5,'R',1,'2018-01-07 20:30:21',9,54,NULL,13,8,4),(109,0,1,'D',1,'2018-01-07 20:30:43',9,8,NULL,13,NULL,NULL),(110,5,2,'R',1,'2018-01-07 20:31:24',6,14,NULL,13,55,2),(111,3,5,'R',1,'2018-01-07 20:32:24',9,8,NULL,13,54,4),(112,5,4,'R',1,'2018-01-07 20:32:38',6,55,NULL,13,14,2),(113,1,5,'R',1,'2018-01-07 20:32:59',54,4,50,13,50,9),(114,2,5,'R',1,'2018-01-07 20:33:19',55,8,NULL,13,6,2),(115,5,3,'R',1,'2018-01-07 20:34:14',9,4,50,13,54,50),(116,3,5,'R',1,'2018-01-07 20:34:45',8,2,NULL,13,55,14),(117,0,5,'R',1,'2018-01-07 20:35:19',9,4,50,13,50,54),(119,5,1,'R',1,'2018-01-07 20:36:57',2,55,NULL,13,8,14),(120,5,4,'R',1,'2018-01-07 20:37:23',55,8,NULL,13,2,14),(121,5,1,'R',1,'2018-01-07 20:37:49',6,1,NULL,13,55,4),(122,4,5,'R',1,'2018-01-07 20:38:43',55,4,NULL,13,8,1),(123,4,5,'R',1,'2018-01-07 20:39:48',1,8,NULL,13,55,4),(124,5,3,'R',1,'2018-01-07 20:40:17',8,4,NULL,13,1,55),(125,0,5,'R',1,'2018-01-07 20:40:46',55,5,NULL,13,8,4),(126,1,0,'D',1,'2018-01-07 20:41:12',55,8,NULL,13,NULL,NULL),(127,5,4,'R',1,'2018-01-07 20:41:47',4,1,NULL,13,9,2),(128,3,5,'R',1,'2018-01-07 20:42:38',7,6,50,13,54,50),(129,3,5,'R',1,'2018-01-07 20:43:23',14,7,NULL,13,4,12),(130,5,2,'R',1,'2018-01-07 20:43:49',1,6,50,13,50,9),(131,5,2,'R',1,'2018-01-07 20:44:07',4,2,NULL,13,12,54),(132,1,5,'R',1,'2018-01-07 20:44:39',14,6,50,13,50,10),(133,5,2,'R',1,'2018-01-07 20:44:58',4,9,NULL,13,12,1),(134,5,0,'R',1,'2018-01-07 20:45:21',10,4,NULL,13,54,14),(135,1,0,'D',1,'2018-01-07 20:45:29',4,14,NULL,13,NULL,NULL),(136,5,0,'R',1,'2018-01-07 20:46:05',10,9,NULL,13,54,12),(137,1,0,'D',1,'2018-01-07 20:46:14',9,12,NULL,13,NULL,NULL),(138,4,5,'R',1,'2018-01-07 20:46:43',7,14,NULL,13,12,1);
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
INSERT INTO `Player` VALUES (1,'.....','aleksandar.gjurcinov@bechtle.com','Aleks','\0','The Sheriff','$2a$10$bBVWmyqbWFKxryYfLoHKuu4FrH9yNIUJRPUXATIb280XFGraaioWK','Gjurcinov'),(2,'...','Rouven.Pfeiffer@bechtle.com','Rouven','\0','The Hornet','$2a$10$VzVWEc3nW6WLpuA.OfBP/easl2LzbwvQMr9VHvbUqA8s0wC5.ZCTu','Pfeiffer'),(3,'...','Enrico.Mischorr@bechtle.com','Enrico','\0','The Wall','$2a$10$ffW9gxr2dJC.y7Iec/UzhuV7Z3Zcn6OoTAijZyU5iBNlEF1muHMQO','Mischorr'),(4,'...','christopher.klein@bechtle.com','Chris','\0','The Arrrrogant Pirat','$2a$10$h3FpGZF3rBTf5HXnvhANJ.1zG1GJ9jDD4kWEfqsTkk5M9BP2.pygS','Klein'),(5,'...','Bastian.Reiss@Bechtle.com','Basti','\0','???','$2a$10$j9A58FhOootJuSvGdcImi.XTBBhYFLsL1lXv4vMfNYKx12dOO/iSa','Reiss'),(6,'...','andreas.nagy@bechtle.com','Andy','\0','Fluffy','$2a$10$kBbvypMOk6MI3VJPC.vHIeiUNn2x50bxzqqE7DKjoi68LsWMuX6ie','Nagy'),(7,'...','maik.lieweries@bechtle.com','Maik','\0','???','$2a$10$Z1e6Cun8jcg/aKF5daKbM.eoOUKTVZ3HnbEzXVqcb6Q7SOL8y9GV6','Lieweris'),(8,'...','andrej.gedert@bechtle.com','Andrej','\0','Dampfhammer','$2a$10$LVoumsYcl319..6lW7aE8emklkrHTHOd852cutZ1eFokABUTHwLqW','Gedert'),(9,'...','hendrik.strenger@bechtle.com','Hendrik','\0','???','$2a$10$zav06WEz8MIpnOPoZ3z4Xe.MTmp6NCtxuajjnFyKWVKgKw4.nV9ei','Strenger'),(10,'...','simon.federau@bechtle.com','Simon','\0','Seuchenvogel','$2a$10$PyxgL.d3SnCtQpmaCHdW7uEhPwEXyJydTEK7iV5YWBBit2oR7jcFm','Federau'),(11,'???','marcus.ehret@bechtle.com','Marcus','\0','???','$2a$10$VCprSWhGUGORj06nYIWU4.yChAz12MvGMjv4Jv4umNa9HEuUMw/v.','Ehret'),(12,'???','ali.erdem@bechtle.com','Ali','\0','Baba','$2a$10$3gwnrs8yKgpPOPNzOBEP8O1e7v6RGBZfu7c6C2V42LqiMogsX7VrK','Erdem'),(14,'???','denis.mylnikov@bechtle.com','Denis','\0','???','$2a$10$1MiG0pFnVMmVW3zk7Cdh4.nLsqnbHyUQ0311l6YyUsMP06Vy6wOhC','Mylnikov'),(50,'From Syria','alaa.bla@bechtle.com','Alaa','','???','$2a$10$O.t4aifGykOcXoOOEANLLOJl6wrN/aeKhZVJ.E.BK9RejyLy4pk7u','Bla'),(54,'Was weiß ich','julian.gleiss@bechtle.com','Julian','\0','The Drückeberger','$2a$10$9w8DzXTmFErgm6c8KZo4G.Fu0I4bhQphao13OlgHyXTSQdhs9Se6C','Gleiß'),(55,'asdsadsa','max.guenther@bechtle.com','Max','\0','Mr. Random','$2a$10$f/uvtxbHjOrvmHRtVQI4dOb5qaQDRreF/auTOuWL.q.39CspNRYte','Günther');
/*!40000 ALTER TABLE `Player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player_Matches`
--

DROP TABLE IF EXISTS `Player_Matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player_Matches` (
  `player_id` bigint(20) NOT NULL,
  `match_id` bigint(20) NOT NULL,
  KEY `FKcwfvw6mdc40kfvhimnv2oy9fv` (`match_id`),
  KEY `FK12maqjb09g1drb8cov9qtpbd` (`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player_Matches`
--

LOCK TABLES `Player_Matches` WRITE;
/*!40000 ALTER TABLE `Player_Matches` DISABLE KEYS */;
INSERT INTO `Player_Matches` VALUES (1,133),(55,125),(2,127),(7,129),(50,130),(1,127),(55,123),(2,119),(54,134),(14,135),(9,136),(50,115),(7,94),(55,121),(1,123),(2,114),(54,128),(14,132),(9,130),(2,110),(54,115),(14,120),(9,115),(2,106),(54,111),(55,119),(9,111),(14,116),(2,100),(50,93),(54,102),(8,125),(54,98),(8,123),(55,114),(54,91),(8,120),(2,95),(8,116),(54,88),(2,90),(9,108),(14,110),(50,87),(8,111),(54,85),(55,110),(9,96),(14,99),(2,87),(54,79),(1,121),(8,108),(2,81),(1,104),(6,130),(54,76),(9,91),(50,77),(4,134),(6,121),(1,101),(2,78),(55,106),(50,75),(4,131),(9,87),(14,96),(1,98),(55,104),(8,106),(7,77),(4,127),(54,74),(2,74),(6,112),(8,104),(9,77),(50,82),(14,89),(55,102),(1,92),(2,76),(54,78),(4,124),(9,75),(14,82),(8,99),(55,98),(1,89),(2,79),(54,81),(14,79),(8,94),(4,122),(1,76),(54,84),(55,92),(2,82),(4,117),(4,113),(1,74),(55,85),(6,103),(4,108),(55,78),(1,78),(8,88),(1,90),(55,74),(4,106),(8,85),(8,81),(4,104),(55,76),(2,85),(4,102),(8,84),(55,81),(2,89),(8,86),(4,99),(54,86),(9,79),(8,90),(9,82),(55,88),(2,92),(14,75),(6,92),(9,88),(8,96),(4,93),(54,89),(6,90),(14,77),(55,94),(2,98),(9,93),(54,95),(4,91),(50,91),(6,95),(55,100),(2,102),(8,101),(54,100),(4,95),(50,96),(9,99),(55,103),(8,105),(2,107),(14,87),(9,109),(4,101),(50,113),(54,108),(8,107),(55,105),(2,112),(14,93),(2,116),(55,107),(8,109),(14,101),(4,103),(55,112),(1,94),(6,110),(55,116),(8,114),(4,105),(1,100),(1,103),(55,120),(4,107),(8,119),(55,122),(4,111),(8,122),(1,105),(8,124),(55,124),(4,115),(5,125),(8,126),(55,126),(4,121),(1,122),(9,113),(2,120),(6,114),(54,113),(50,117),(7,75),(4,123),(14,112),(7,128),(12,137),(6,128),(50,128),(1,124),(9,117),(4,125),(12,133),(2,131),(54,117),(6,132),(50,132),(14,119),(10,134),(4,129),(12,129),(1,130),(9,127),(14,129),(10,132),(54,131),(4,133),(4,135),(14,134),(10,136),(54,136),(9,133),(12,131),(9,137),(12,136),(12,138),(14,138),(1,138),(7,138);
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
  KEY `FK79lv38g6wglov7w1g9p2me8n0` (`matches_id`),
  KEY `FKgb5qpk6bg8di5ne613pyie938` (`Season_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Season_Matches`
--

LOCK TABLES `Season_Matches` WRITE;
/*!40000 ALTER TABLE `Season_Matches` DISABLE KEYS */;
INSERT INTO `Season_Matches` VALUES (13,137),(13,135),(13,133),(13,131),(13,129),(13,127),(13,125),(13,123),(13,121),(13,117),(13,115),(13,113),(13,111),(13,109),(13,107),(13,105),(13,103),(13,101),(13,99),(13,98),(13,95),(13,93),(13,91),(13,89),(13,87),(13,81),(13,79),(13,77),(13,75),(13,74),(13,76),(13,78),(13,82),(13,84),(13,85),(13,86),(13,88),(13,90),(13,92),(13,94),(13,96),(13,100),(13,102),(13,104),(13,106),(13,108),(13,110),(13,112),(13,114),(13,116),(13,119),(13,120),(13,122),(13,124),(13,126),(13,128),(13,130),(13,132),(13,134),(13,136),(13,138);
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
INSERT INTO `hibernate_sequence` VALUES (139),(139),(139);
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

-- Dump completed on 2018-01-07 20:50:41
