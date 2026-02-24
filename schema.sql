-- ============================================================
-- GreenGrocer Database Schema
-- CMPE343 – Software Engineering | Bahçeşehir University
-- ============================================================
-- Run this script to create and populate the database.
-- Images are loaded separately via the application (AddProductImages.java).
-- ============================================================

CREATE DATABASE IF NOT EXISTS `greengrocer`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `greengrocer`;

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- TABLE: userinfo
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
  `id`       INT          NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50)  NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role`     VARCHAR(20)  NOT NULL DEFAULT 'customer',
  `address`  VARCHAR(255) DEFAULT NULL,
  `phone`    VARCHAR(20)  DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: Product
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `Product`;
CREATE TABLE `Product` (
  `product_id`       INT          NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(100) NOT NULL,
  `price`            DOUBLE       NOT NULL,
  `stock`            DOUBLE       NOT NULL,
  `type`             VARCHAR(30)  NOT NULL DEFAULT 'fruit',
  `threshold`        INT          NOT NULL DEFAULT 5,
  `image`            LONGBLOB     DEFAULT NULL,
  `discount_percent` DOUBLE       DEFAULT 0,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: Orders
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `Orders`;
CREATE TABLE `Orders` (
  `order_id`           INT          NOT NULL AUTO_INCREMENT,
  `username`           VARCHAR(50)  NOT NULL,
  `product_id`         INT          DEFAULT NULL,
  `quantity`           DOUBLE       DEFAULT NULL,
  `total`              DOUBLE       NOT NULL DEFAULT 0,
  `status`             VARCHAR(20)  NOT NULL DEFAULT 'NEW',
  `created_at`         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  `requested_delivery` DATETIME     DEFAULT NULL,
  `delivered_at`       DATETIME     DEFAULT NULL,
  `carrier_username`   VARCHAR(50)  DEFAULT NULL,
  `total_cost`         DOUBLE       NOT NULL DEFAULT 0,
  PRIMARY KEY (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: OrderItems
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `OrderItems`;
CREATE TABLE `OrderItems` (
  `item_id`        INT    NOT NULL AUTO_INCREMENT,
  `order_id`       INT    NOT NULL,
  `product_id`     INT    NOT NULL,
  `kg`             DOUBLE NOT NULL,
  `price_at_time`  DOUBLE NOT NULL,
  PRIMARY KEY (`item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`order_id`)   REFERENCES `Orders`  (`order_id`),
  CONSTRAINT `orderitems_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: Invoice
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `Invoice`;
CREATE TABLE `Invoice` (
  `invoice_id`   INT          NOT NULL AUTO_INCREMENT,
  `order_id`     INT          NOT NULL,
  `username`     VARCHAR(50)  NOT NULL,
  `issued_at`    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  `total_amount` DOUBLE       NOT NULL,
  `pdf_path`     VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`invoice_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: Coupon
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `Coupon`;
CREATE TABLE `Coupon` (
  `coupon_id`       INT          NOT NULL AUTO_INCREMENT,
  `code`            VARCHAR(50)  NOT NULL UNIQUE,
  `discount_amount` DOUBLE       NOT NULL DEFAULT 0,
  `discount_percent`DOUBLE       NOT NULL DEFAULT 0,
  `is_active`       BOOLEAN      DEFAULT TRUE,
  `expiry_date`     DATE         DEFAULT NULL,
  `assigned_user_id`INT          DEFAULT NULL,
  `description`     VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX IF NOT EXISTS `idx_coupon_user` ON `Coupon`(`assigned_user_id`);

-- ------------------------------------------------------------
-- TABLE: CustomerLoyalty
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `CustomerLoyalty`;
CREATE TABLE `CustomerLoyalty` (
  `id`           INT         NOT NULL AUTO_INCREMENT,
  `username`     VARCHAR(50) NOT NULL UNIQUE,
  `points`       INT         NOT NULL DEFAULT 0,
  `total_spent`  DOUBLE      NOT NULL DEFAULT 0,
  `level`        VARCHAR(20) NOT NULL DEFAULT 'BRONZE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: CarrierRating
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `CarrierRating`;
CREATE TABLE `CarrierRating` (
  `rating_id`        INT         NOT NULL AUTO_INCREMENT,
  `order_id`         INT         NOT NULL,
  `customer_username`VARCHAR(50) NOT NULL,
  `carrier_username` VARCHAR(50) NOT NULL,
  `rating`           INT         NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
  `comment`          TEXT        DEFAULT NULL,
  `created_at`       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`rating_id`),
  UNIQUE KEY `unique_rating` (`order_id`),
  CONSTRAINT `carrierrating_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: Message
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `Message`;
CREATE TABLE `Message` (
  `message_id`  INT         NOT NULL AUTO_INCREMENT,
  `sender`      VARCHAR(50) NOT NULL,
  `receiver`    VARCHAR(50) NOT NULL,
  `content`     TEXT        NOT NULL,
  `sent_at`     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  `is_read`     BOOLEAN     DEFAULT FALSE,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: SystemMessage
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `SystemMessage`;
CREATE TABLE `SystemMessage` (
  `id`                 INT          NOT NULL AUTO_INCREMENT,
  `message_type`       VARCHAR(50)  NOT NULL,
  `title`              VARCHAR(100) NOT NULL,
  `message`            TEXT         NOT NULL,
  `related_product_id` INT          DEFAULT NULL,
  `created_at`         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  `is_read`            BOOLEAN      DEFAULT FALSE,
  PRIMARY KEY (`id`),
  CONSTRAINT `sysmsg_ibfk_1` FOREIGN KEY (`related_product_id`) REFERENCES `Product` (`product_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABLE: OrderStatusHistory
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `OrderStatusHistory`;
CREATE TABLE `OrderStatusHistory` (
  `history_id`  INT         NOT NULL AUTO_INCREMENT,
  `order_id`    INT         NOT NULL,
  `status`      VARCHAR(20) NOT NULL,
  `changed_at`  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  `changed_by`  VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  CONSTRAINT `osh_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Default users (passwords are plaintext for demo purposes)
-- owner / owner123, carrier / carr, customer / cust
INSERT INTO `userinfo` (`username`, `password`, `role`, `address`, `phone`) VALUES
('owner',  'owner123', 'owner',    'Kadıköy',    '5550000001'),
('carr',   'carr',     'carrier',  'Beşiktaş',   '5550000002'),
('cust',   'cust',     'customer', 'Şişli',      '5550000003');

-- Sample products (no images — load via application)
INSERT INTO `Product` (`name`, `price`, `stock`, `type`, `threshold`, `discount_percent`) VALUES
('Apple (1kg)',        45,  38.25, 'fruit',     25, 0),
('Banana (1kg)',       55,   8.75, 'fruit',      5, 0),
('Olive Oil (1L)',    210,   9.50, 'grocery',    5, 0),
('Tomato (1kg)',       30,  52.00, 'vegetable',  5, 0),
('Potato (1kg)',       20,  80.00, 'vegetable',  5, 0),
('Onion (1kg)',        18,  85.25, 'vegetable',  5, 0),
('Cucumber (1kg)',     25,  40.50, 'vegetable',  5, 0),
('Orange (1kg)',       40,  40.00, 'fruit',      5, 0),
('Lemon (1kg)',        35,  35.00, 'fruit',      5, 0),
('Strawberry (500g)', 70,  20.00, 'fruit',      5, 0),
('Grapes (1kg)',       65,  22.00, 'fruit',      5, 0),
('Milk (1L)',          28,  50.00, 'dairy',      5, 0),
('Yogurt (1kg)',       45,  30.00, 'dairy',      5, 0),
('Cheese (500g)',      90,  25.00, 'dairy',      5, 0),
('Bread',             10, 100.00, 'bakery',      5, 0),
('Eggs (10)',          55,  40.00, 'dairy',       5, 0),
('Rice (1kg)',         60,  35.00, 'grocery',    5, 0),
('Pasta (500g)',       25,  70.00, 'grocery',    5, 0),
('Chicken (1kg)',     120,  18.00, 'meat',        5, 0),
('Beef (1kg)',        320,  10.00, 'meat',        5, 0),
('Fish (1kg)',        200,  12.00, 'meat',        5, 0),
('Water (1.5L)',        8, 150.00, 'beverage',   5, 0),
('Tea (500g)',        110,  20.00, 'beverage',   5, 0),
('Coffee (250g)',     140,  15.00, 'beverage',   5, 0),
('Sugar (1kg)',        50,  40.00, 'grocery',    5, 0);

-- Sample coupon
INSERT INTO `Coupon` (`code`, `discount_percent`, `description`, `is_active`) VALUES
('WELCOME10', 10, '10% off for new customers', TRUE);

SET FOREIGN_KEY_CHECKS = 1;
