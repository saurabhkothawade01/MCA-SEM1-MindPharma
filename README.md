## Database Structure

show databases;

create database mindpharma;

use mindpharma;

show tables;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `photo` longblob,
  PRIMARY KEY (`id`)
);

CREATE TABLE `company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `contact_no` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `medicine` (
  `id` int NOT NULL AUTO_INCREMENT,
  `medicine_name` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `category` enum('Tablets','Capsules','Syrups','Injections') DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price_per_unit` decimal(10,2) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `sales` (
  `sale_id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `contact_no` varchar(20) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `sale_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sale_id`)
);

CREATE TABLE `sale_details` (
  `sale_id` int DEFAULT NULL,
  `medicine_name` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `medicine_price` double DEFAULT NULL,
  `line_total` double DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  KEY `sale_id` (`sale_id`),
  CONSTRAINT `sale_details_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`sale_id`)
);

show tables;

select * from user;	

select * from company;

select * from medicine;

select * from sales;

select * from sale_details;
