SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddy-db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `paymybuddy-db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `paymybuddy-db` ;

-- -----------------------------------------------------
-- Table `paymybuddy-db`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy-db`.`user` (
  `account_non_expired` BIT(1) NOT NULL,
  `account_non_locked` BIT(1) NOT NULL,
  `credentials_non_expired` BIT(1) NOT NULL,
  `is_enabled` BIT(1) NOT NULL,
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_ob8kqyqqgmefl0aco34akdtpe` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `paymybuddy-db`.`account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy-db`.`account` (
  `balance` DOUBLE NOT NULL,
  `id_account` BIGINT NOT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id_account`),
  UNIQUE INDEX `UK_h6dr47em6vg85yuwt4e2roca4` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK7m8ru44m93ukyb61dfxw0apf6`
    FOREIGN KEY (`user_id`)
    REFERENCES `paymybuddy-db`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `paymybuddy-db`.`account_id_sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy-db`.`account_id_sequence` (
  `next_val` BIGINT NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `paymybuddy-db`.`bank_account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy-db`.`bank_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NULL DEFAULT NULL,
  `iban` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_ss4uej5gx2a07srb540l15s21` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK92iik4jwhk7q385jubl2bc2mm`
    FOREIGN KEY (`user_id`)
    REFERENCES `paymybuddy-db`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `paymybuddy-db`.`friends`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy-db`.`friends` (
  `friend_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`friend_id`, `user_id`),
  INDEX `FKivlhvh7odettdl818ml67apb9` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKivlhvh7odettdl818ml67apb9`
    FOREIGN KEY (`user_id`)
    REFERENCES `paymybuddy-db`.`user` (`id`),
  CONSTRAINT `FKqhlqyd2eb3nmk9hvrfqslw918`
    FOREIGN KEY (`friend_id`)
    REFERENCES `paymybuddy-db`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `paymybuddy-db`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy-db`.`transaction` (
  `amount` DOUBLE NOT NULL,
  `date_transaction` DATETIME(6) NULL DEFAULT NULL,
  `id_transaction` BIGINT NOT NULL AUTO_INCREMENT,
  `receiver_user_account` BIGINT NULL DEFAULT NULL,
  `sender_user_account` BIGINT NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `transaction_type` ENUM('FROM_BANK', 'TO_BANK', 'TO_FRIEND') NULL DEFAULT NULL,
  PRIMARY KEY (`id_transaction`),
  INDEX `FKgcolwsqhbd81mwk61w2vuilm1` (`receiver_user_account` ASC) VISIBLE,
  INDEX `FKbxnvo1ujlg87ovjiamir3ajmf` (`sender_user_account` ASC) VISIBLE,
  CONSTRAINT `FKbxnvo1ujlg87ovjiamir3ajmf`
    FOREIGN KEY (`sender_user_account`)
    REFERENCES `paymybuddy-db`.`account` (`id_account`),
  CONSTRAINT `FKgcolwsqhbd81mwk61w2vuilm1`
    FOREIGN KEY (`receiver_user_account`)
    REFERENCES `paymybuddy-db`.`account` (`id_account`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;