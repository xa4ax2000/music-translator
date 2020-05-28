CREATE DATABASE `music`;

USE `music`;

CREATE TABLE `users` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(60) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `firstName` varchar(100) DEFAULT NULL,
  `lastName` varchar(100) DEFAULT NULL,
  `userType` varchar(2) NOT NULL,
  `refreshToken` varchar(255) DEFAULT NULL,
  `isDisabled` tinyint(1) DEFAULT '0',
  `lastLoginDateTime` datetime DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`userId`)
);
