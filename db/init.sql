-- MariaDB dump 10.19-11.8.6-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: shelter
-- ------------------------------------------------------
-- Server version	8.4.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Current Database: `shelter`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `shelter` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `shelter`;

--
-- Table structure for table `adoptions`
--

DROP TABLE IF EXISTS `adoptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ending_date` date DEFAULT NULL,
  `initial_date` date NOT NULL,
  `status` enum('ACCEPTED','DECLINED','PENDING') NOT NULL,
  `user_id` bigint NOT NULL,
  `pet_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK62lsvmw4ij0kknnc8eaixicsl` (`user_id`),
  KEY `FKey4s7xb6gkguogi15tytsi706` (`pet_id`),
  CONSTRAINT `FK62lsvmw4ij0kknnc8eaixicsl` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKey4s7xb6gkguogi15tytsi706` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adoptions`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `adoptions` WRITE;
/*!40000 ALTER TABLE `adoptions` DISABLE KEYS */;
INSERT INTO `adoptions` VALUES
(1,'2026-10-10','2026-10-10','ACCEPTED',3,3),
(5,NULL,'2026-10-20','PENDING',3,10),
(6,NULL,'2026-10-20','PENDING',3,11),
(7,NULL,'2026-10-20','PENDING',2,8);
/*!40000 ALTER TABLE `adoptions` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `foster_homes`
--

DROP TABLE IF EXISTS `foster_homes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `foster_homes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `capacity` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `province` enum('ALAVA','ALBACETE','ALICANTE','ALMERIA','ASTURIAS','AVILA','A_CORUNA','BADAJOZ','BARCELONA','BIZKAIA','BURGOS','CACERES','CADIZ','CANTABRIA','CASTELLON','CIUDAD_REAL','CORDOBA','CUENCA','GIPUZKOA','GIRONA','GRANADA','GUADALAJARA','HUELVA','HUESCA','ISLAS_BALEARES','JAEN','LAS_PALMAS','LA_RIOJA','LEON','LLEIDA','LUGO','MADRID','MALAGA','MURCIA','NAVARRA','OURENSE','PALENCIA','PONTEVEDRA','SALAMANCA','SANTA_CRUZ_DE_TENERIFE','SEGOVIA','SEVILLA','SORIA','TARRAGONA','TERUEL','TOLEDO','VALENCIA','VALLADOLID','ZAMORA','ZARAGOZA') NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkjng8oh83kyal9o4m5ijo29h8` (`user_id`),
  CONSTRAINT `FKkjng8oh83kyal9o4m5ijo29h8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `foster_homes`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `foster_homes` WRITE;
/*!40000 ALTER TABLE `foster_homes` DISABLE KEYS */;
INSERT INTO `foster_homes` VALUES
(1,2,'casa coruña','A_CORUNA',2),
(4,4,'Casa Fernando','PONTEVEDRA',7);
/*!40000 ALTER TABLE `foster_homes` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age_group` enum('ADULT','BABY','SENIOR','YOUNG') DEFAULT NULL,
  `breed` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `info` mediumtext,
  `name` varchar(255) NOT NULL,
  `registered_at` date NOT NULL,
  `species` enum('CAT','DOG','OTHER') NOT NULL,
  `foster_home_id` bigint NOT NULL,
  `status` enum('ADOPTED','AVAILABLE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk0916x8vf2rstjlcqn6boqalm` (`foster_home_id`),
  CONSTRAINT `FKk0916x8vf2rstjlcqn6boqalm` FOREIGN KEY (`foster_home_id`) REFERENCES `foster_homes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES
(1,'YOUNG',NULL,NULL,'Es asustadiza con las personas pero puede convivir con perros si estos no son agresivos','Blanquita','2026-10-10','CAT',1,'AVAILABLE'),
(3,'ADULT','San Bernardo','2/842da2d6-30c6-49b7-8aa5-ae3bafbb2ae6.jpeg','Hijo de Fofo','Fofito','2026-10-10','DOG',1,'ADOPTED'),
(4,'ADULT','San Bernardo','2/b77373cd-68c6-4e3c-8ed1-3a12c6dfe2b2.jpeg','\"Es algo mayor, le gusta estar en casa, puede convivir con gatos, pero no le gustan mucho los perros\"','Fofo','2026-10-16','DOG',1,'AVAILABLE'),
(5,'ADULT','Común europeo de pelo semilargo','2/b112c5ba-67c9-4ede-a226-c28476ce7331.jpg','Gata tricolor de unos 4 años, muy tranquila y cariñosa. Le encanta tumbarse en el sofá y que la cepillen. Está esterilizada, vacunada y con microchip. Se lleva bien con otros gatos y es ideal para un piso.','Canela','2026-10-17','CAT',1,'AVAILABLE'),
(6,'ADULT','Mestizo','2/4ce420c1-3431-42a3-82e5-5945fe35a4da.jpg','Macho de unos 5 años y tamaño mediano (16 kg). Al principio es algo tímido con la gente nueva, pero en cuanto coge confianza es un perro noble y muy apegado. Pasea bien con correa y es tranquilo en casa. Vacunado, desparasitado y castrado.','Toffee','2026-10-17','DOG',1,'AVAILABLE'),
(7,'YOUNG','Común europeo atigrado','7/67cc3202-a70c-42eb-9095-270dd494caa3.jpg','Gato joven de un año y medio, atigrado con pecho blanco. Es muy juguetón, curioso y hablador: te contará cómo le ha ido el día. Castrado y vacunado. Busca una familia activa que le dedique tiempo de juego.','Bigotes','2026-10-17','CAT',4,'AVAILABLE'),
(8,'BABY','Mestiza de border collie','7/4cf83f44-d767-494b-ad23-12b24e9f2d54.jpg','Cachorra de 4 meses, blanca y negra, muy lista y con mucha energía. Aprende rápido y ya hace sus necesidades en la calle. Necesita una familia con tiempo para educarla y sacarla a correr. Tiene las primeras vacunas y microchip.','Luna','2026-10-17','DOG',4,'AVAILABLE'),
(9,'BABY','Mestizo','7/8e099590-49a7-4034-b7ca-bcb73a2724df.jpg','Cachorro de 5 meses encontrado en el campo junto a sus hermanos. Es dulce, sociable con otros perros y con niños, y un poco dormilón. De adulto será de tamaño mediano. Vacunado y desparasitado.','Nilo','2026-10-17','DOG',4,'AVAILABLE'),
(10,'ADULT','Pastor alemán','2/77a4417f-a0fa-42a0-a266-38264ae8b471.png','Perro tranquilo y muy leal, ideal para familias con jardín.','Rocky','2026-10-20','DOG',1,'AVAILABLE'),
(11,'YOUNG','Común europeo','2/c2181f72-4346-444b-97a9-7b5e7aca0207.png','Gato juguetón y curioso, se lleva bien con otros gatos.','Simba','2026-10-20','CAT',1,'AVAILABLE'),
(13,'BABY','Labrador','2/73529897-173a-4d6a-a7c6-0d8e1bab3e10.jpg','Cachorro lleno de energía, necesita paseos diarios.','Thor','2026-10-20','DOG',1,'AVAILABLE');
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ROLE_ADMIN','ROLE_USER') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(1,'admin@shelter.com','$2a$10$ycJ71elqClZKnVmQKxngpOa4xruBe0xcaVY68y6SoEjU.Bv5n4Rc6','ROLE_ADMIN','admin'),
(2,'david@shelter.com','$2a$10$SHLLDo9tyn1NOz0/Uhvv/.sIPnrp.KV9Da5d2I2CP2PY/kIydGYBO','ROLE_USER','david'),
(3,'patricia@shelter.com','$2a$10$6pW1nvWmEqK5bFFEVZYz0uG8UuVXBi/Q5f68pm4OYNA.i.FAgl3M2','ROLE_USER','patricia'),
(6,'alan@shelter.com','$2a$10$hiYhbYFKP7qHGLymtzVzROf4dGEyAUzkAAotY0e8A4z/w7tAGI1S6','ROLE_USER','alan'),
(7,'fernando@shelter.com','$2a$10$AlzmiSnjcszN0uQK2.Sdgu3x9omurx2xLUgqNsPdtnBTJcJpmmWPq','ROLE_USER','fernando');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-10-31 18:39:17
