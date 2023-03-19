CREATE TABLE `bank_account` (
    `iban` varchar(34) COLLATE utf8_bin NOT NULL,
    `bic` varchar(50) COLLATE utf8_bin NOT NULL,
    `bankName` varchar(50) COLLATE utf8_bin NOT NULL,
    `accountName` varchar(50) COLLATE utf8_bin NOT NULL,
    `user_id` int NOT NULL,PRIMARY KEY (`iban`),
    KEY `fk_bank_account_user1_idx` (`user_id`),
    CONSTRAINT `fk_bank_account_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE `external_transfer` (`transfer_id` int NOT NULL,`fees` decimal(9,2) NOT NULL,`bank_account_iban` varchar(34) COLLATE utf8_bin NOT NULL,PRIMARY KEY (`transfer_id`),KEY `fk_external_transfer_bank_account1` (`bank_account_iban`),CONSTRAINT `fk_external_transfer_bank_account1` FOREIGN KEY (`bank_account_iban`) REFERENCES `bank_account` (`iban`),CONSTRAINT `fk_external_transfer_transfer1` FOREIGN KEY (`transfer_id`) REFERENCES `transfer` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE `internal_transfer` (
                                     `transfer_id` int NOT NULL,
                                     `sender` int NOT NULL,
                                     `receiver` int NOT NULL,
                                     PRIMARY KEY (`transfer_id`),
    KEY `fk_internal_transfer_user1_idx` (`sender`),
    KEY `fk_internal_transfer_user2_idx` (`receiver`),
    CONSTRAINT `fk_internal_transfer_transfer1` FOREIGN KEY (`transfer_id`) REFERENCES `transfer` (`id`),
    CONSTRAINT `fk_internal_transfer_user1` FOREIGN KEY (`sender`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_internal_transfer_user2` FOREIGN KEY (`receiver`) REFERENCES `user` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE `relation` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `owner` int NOT NULL,
                            `buddy` int NOT NULL,
                            PRIMARY KEY (`id`),
    KEY `FK_relation_user1_idx` (`owner`),
    KEY `FK_relation_user2_idx` (`buddy`),
    CONSTRAINT `FK_relation_user1` FOREIGN KEY (`owner`) REFERENCES `user` (`id`),
    CONSTRAINT `FK_relation_user2` FOREIGN KEY (`buddy`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
    ) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE `role` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE `transfer` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `amount` decimal(9,2) NOT NULL,
    `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `transactionDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;


CREATE TABLE `user` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `firstname` varchar(45) COLLATE utf8_bin NOT NULL,
    `lastname` varchar(45) COLLATE utf8_bin NOT NULL,
    `email` varchar(60) COLLATE utf8_bin NOT NULL,
    `password` varchar(255) COLLATE utf8_bin NOT NULL,
    `balance` decimal(9,2) NOT NULL DEFAULT '0.00',
    `createDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `email_UNIQUE` (`email`)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

