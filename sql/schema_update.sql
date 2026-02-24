-- GreenGrocer Database Schema Updates
-- Run this script to add new features

-- 1. Product Discount Support
ALTER TABLE Product ADD COLUMN IF NOT EXISTS discount_percent DOUBLE DEFAULT 0;

-- 2. Coupon User Assignment (NULL = all users)
ALTER TABLE Coupon ADD COLUMN IF NOT EXISTS assigned_user_id INT DEFAULT NULL;
ALTER TABLE Coupon ADD COLUMN IF NOT EXISTS description VARCHAR(255);

-- 3. System Messages for Owner (stock alerts, etc.)
CREATE TABLE IF NOT EXISTS SystemMessage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message_type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    related_product_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (related_product_id) REFERENCES Product(product_id) ON DELETE SET NULL
);

-- 4. Index for faster coupon lookups by user
CREATE INDEX IF NOT EXISTS idx_coupon_user ON Coupon(assigned_user_id);
