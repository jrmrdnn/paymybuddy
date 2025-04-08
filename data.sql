DROP TABLE IF EXISTS `transactions`;
DROP TABLE IF EXISTS `relations`;
DROP TABLE IF EXISTS `bank_accounts`;
DROP TABLE IF EXISTS `users`;

CREATE DATABASE IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `paymybuddy`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime,
  `deleted_at` datetime, 
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `bank_accounts` (
  `user_id` int NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT 0.00 CHECK (`balance` >= 0),
  `account_number` varchar(50) UNIQUE DEFAULT NULL,
  `iban` varchar(34) UNIQUE DEFAULT NULL,
  `bic` varchar(11) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `relations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `relation_user_id` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`relation_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL CHECK (`amount` > 0),
  `description` varchar(50) NOT NULL,
  `receiver_user_id` int NOT NULL,
  `sender_user_id` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`receiver_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`sender_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `users` (`id`, `username`, `email`, `password`, `created_at`) VALUES
(1, 'John', 'john@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-01 00:00:00'),
(2, 'Tom', 'tom@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-02 00:00:00'),
(3, 'Alice', 'alice@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-03 00:00:00'),
(4, 'Bob', 'bob@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-04 00:00:00'),
(5, 'Charlie', 'charlie@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-05 00:00:00'),
(6, 'David', 'david@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-06 00:00:00'),
(7, 'Eve', 'eve@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-07 00:00:00'),
(8, 'Frank', 'frank@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-08 00:00:00'),
(9, 'Grace', 'grace@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-09 00:00:00'),
(10, 'Helen', 'helen@mail.com', '$2a$10$6qkJ0dPqYlxgALjdmxQtpuqMBXPBOWPqE2Q1H8t3wsqQNajPwCkuu', '2020-01-10 00:00:00');

INSERT INTO `bank_accounts` (`user_id`, `account_number`, `balance`, `iban`, `bic`, `created_at`) VALUES
(1, '1234567890', 10000.00, 'FR1420041010050500013M02601', 'PSSTFRPPNTE', '2020-01-01 00:00:00'),
(2, '2345678901', 10000.00, 'FR1420041010050500013M02602', 'PSSTFRPPNTE', '2020-01-02 00:00:00'),
(3, '3456789012', 10000.00, 'FR1420041010050500013M02603', 'PSSTFRPPNTE', '2020-01-03 00:00:00'),
(4, '4567890123', 10000.00, 'FR1420041010050500013M02604', 'PSSTFRPPNTE', '2020-01-04 00:00:00'),
(5, '5678901234', 10000.00, 'FR1420041010050500013M02605', 'PSSTFRPPNTE', '2020-01-05 00:00:00'),
(6, '6789012345', 10000.00, 'FR1420041010050500013M02606', 'PSSTFRPPNTE', '2020-01-06 00:00:00'),
(7, '7890123456', 10000.00, 'FR1420041010050500013M02607', 'PSSTFRPPNTE', '2020-01-07 00:00:00'),
(8, '8901234567', 10000.00, 'FR1420041010050500013M02608', 'PSSTFRPPNTE', '2020-01-08 00:00:00'),
(9, '9012345678', 10000.00, 'FR1420041010050500013M02609', 'PSSTFRPPNTE', '2020-01-09 00:00:00'),
(10, '0123456789', 10000.00, 'FR1420041010050500013M02600', 'PSSTFRPPNTE', '2020-01-10 00:00:00');

INSERT INTO `relations` (`user_id`, `relation_user_id`, `created_at`) VALUES
(1, 2, '2020-01-01 00:00:00'),
(1, 4, '2020-01-01 00:00:00'),
(1, 6, '2020-01-01 00:00:00'),
(1, 8, '2020-01-01 00:00:00'),
(1, 10, '2020-01-01 00:00:00'),
(2, 1, '2020-01-02 00:00:00'),
(2, 3, '2020-01-02 00:00:00'),
(2, 5, '2020-01-02 00:00:00'),
(2, 7, '2020-01-02 00:00:00'),
(2, 9, '2020-01-02 00:00:00'),
(3, 2, '2020-01-03 00:00:00'),
(3, 4, '2020-01-03 00:00:00'),
(3, 6, '2020-01-03 00:00:00'),
(3, 8, '2020-01-03 00:00:00'),
(3, 10, '2020-01-03 00:00:00'),
(4, 1, '2020-01-04 00:00:00'),
(4, 3, '2020-01-04 00:00:00'),
(4, 5, '2020-01-04 00:00:00'),
(4, 7, '2020-01-04 00:00:00'),
(4, 9, '2020-01-04 00:00:00'),
(5, 2, '2020-01-05 00:00:00'),
(5, 4, '2020-01-05 00:00:00'),
(5, 6, '2020-01-05 00:00:00'),
(5, 8, '2020-01-05 00:00:00'),
(5, 10, '2020-01-05 00:00:00'),
(6, 1, '2020-01-06 00:00:00'),
(6, 3, '2020-01-06 00:00:00'),
(6, 5, '2020-01-06 00:00:00'),
(6, 7, '2020-01-06 00:00:00'),
(6, 9, '2020-01-06 00:00:00'),
(7, 2, '2020-01-07 00:00:00'),
(7, 4, '2020-01-07 00:00:00'),
(7, 6, '2020-01-07 00:00:00'),
(7, 8, '2020-01-07 00:00:00'),
(7, 10, '2020-01-07 00:00:00'),
(8, 1, '2020-01-08 00:00:00'),
(8, 3, '2020-01-08 00:00:00'),
(8, 5, '2020-01-08 00:00:00'),
(8, 7, '2020-01-08 00:00:00'),
(8, 9, '2020-01-08 00:00:00'),
(9, 2, '2020-01-09 00:00:00'),
(9, 4, '2020-01-09 00:00:00'),
(9, 6, '2020-01-09 00:00:00'),
(9, 8, '2020-01-09 00:00:00'),
(9, 10, '2020-01-09 00:00:00'),
(10, 1, '2020-01-10 00:00:00'),
(10, 3, '2020-01-10 00:00:00'),
(10, 5, '2020-01-10 00:00:00'),
(10, 7, '2020-01-10 00:00:00'),
(10, 9, '2020-01-10 00:00:00');

DROP PROCEDURE IF EXISTS `GenerateTransactions`;

DELIMITER //
CREATE PROCEDURE GenerateTransactions()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE max_id INT;
    SET max_id = 1000000;

    WHILE i <= max_id DO
        INSERT INTO `transactions` (`id`, `amount`, `description`, `receiver_user_id`, `sender_user_id`, `created_at`)
        VALUES (
            i,
            ROUND(RAND() * 9 + 1, 2),
            ELT(FLOOR(RAND() * 10) + 1, 'Cinéma', 'Restaurant', 'Shopping', 'Voyage', 'Concert', 'Théâtre', 'Musée', 'Exposition', 'Festival', 'Spectacle'),
            FLOOR(RAND() * 10) + 1,
            FLOOR(RAND() * 10) + 1,
            DATE_ADD('2021-01-01', INTERVAL FLOOR(RAND() * 365) DAY)
        );
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

CALL GenerateTransactions();