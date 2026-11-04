
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `fund_trade` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `fund_trade`;
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operation` varchar(64) NOT NULL,
  `user_id` varchar(64) DEFAULT NULL,
  `result` varchar(16) NOT NULL,
  `request_summary` varchar(1000) DEFAULT NULL,
  `error_type` varchar(500) DEFAULT NULL,
  `duration_ms` bigint NOT NULL,
  `request_uri` varchar(200) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_log_created_at` (`created_at`),
  KEY `idx_audit_log_operation` (`operation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_request_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `product_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `shares` bigint NOT NULL,
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_purchase_order_order_id` (`order_id`),
  UNIQUE KEY `uk_purchase_order_client_request_id` (`client_request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `local_message`;
CREATE TABLE `local_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `topic` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` int NOT NULL,
  `retry_count` int NOT NULL,
  `max_retry` int NOT NULL,
  `next_retry_time` datetime(6) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_local_message_message_id` (`message_id`),
  KEY `idx_local_message_retry` (`status`,`next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
DROP TABLE IF EXISTS `ta_confirm_record`;
CREATE TABLE `ta_confirm_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `confirm_status` int NOT NULL,
  `confirmed_amount` bigint NOT NULL,
  `fail_reason` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `confirm_date` date NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ta_confirm_record_order_id` (`order_id`),
  KEY `idx_ta_confirm_record_date` (`confirm_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
DROP TABLE IF EXISTS `reconciliation_error`;
CREATE TABLE `reconciliation_error` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `report_id` bigint NOT NULL,
  `error_type` varchar(32) NOT NULL,
  `order_id` varchar(64) DEFAULT NULL,
  `system_amount` bigint DEFAULT NULL,
  `system_status` varchar(32) DEFAULT NULL,
  `ta_amount` bigint DEFAULT NULL,
  `ta_status` int DEFAULT NULL,
  `resolved` bit NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_reconciliation_error_report_id` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
DROP TABLE IF EXISTS `reconciliation_report`;
CREATE TABLE `reconciliation_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `report_date` date NOT NULL,
  `total_orders` int NOT NULL,
  `matched_count` int NOT NULL,
  `mismatch_count` int NOT NULL,
  `missing_in_ta` int NOT NULL,
  `missing_in_system` int NOT NULL,
  `status` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reconciliation_report_date` (`report_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
DROP TABLE IF EXISTS `redemption_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `redemption_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_request_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `product_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `shares` bigint NOT NULL,
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_redemption_order_order_id` (`order_id`),
  UNIQUE KEY `uk_redemption_order_client_request_id` (`client_request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `fund_asset` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `fund_asset`;
DROP TABLE IF EXISTS `pending_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pending_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `product_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `shares` bigint NOT NULL,
  `status` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pending_asset_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `position` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `shares` bigint NOT NULL,
  `frozen_shares` bigint NOT NULL,
  `version` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_position_user_product` (`user_id`,`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `fund_payment` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `fund_payment`;
DROP TABLE IF EXISTS `payment_transaction`;
CREATE TABLE `payment_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `amount` bigint NOT NULL,
  `operation` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_transaction_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `payment_account`;
CREATE TABLE `payment_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `available_balance` bigint NOT NULL,
  `frozen_balance` bigint NOT NULL,
  `version` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_account_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
