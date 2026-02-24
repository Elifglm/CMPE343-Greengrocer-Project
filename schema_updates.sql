-- GreenGrocer Database Schema Updates
-- Run this SQL in MySQL to add new tables

-- =====================================================
-- 1. Invoice & Transaction Log Table
-- =====================================================
CREATE TABLE IF NOT EXISTS Invoice (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    invoice_pdf LONGBLOB,
    invoice_content LONGTEXT,  -- CLOB for invoice text content
    transaction_log LONGTEXT,  -- CLOB for transaction log
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE
);

-- =====================================================
-- 2. Order Status History Table
-- =====================================================
CREATE TABLE IF NOT EXISTS OrderStatusHistory (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(50),
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE
);

-- =====================================================
-- 3. Carrier Rating Table
-- =====================================================
CREATE TABLE IF NOT EXISTS CarrierRating (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    carrier_username VARCHAR(50) NOT NULL,
    customer_username VARCHAR(50) NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE
);

-- =====================================================
-- 4. Messages Table (Customer-Owner Communication)
-- =====================================================
CREATE TABLE IF NOT EXISTS Messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_username VARCHAR(50) NOT NULL,
    receiver_username VARCHAR(50) NOT NULL,
    subject VARCHAR(200),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    parent_message_id INT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_message_id) REFERENCES Messages(message_id) ON DELETE SET NULL
);

-- =====================================================
-- 5. Coupon Table
-- =====================================================
CREATE TABLE IF NOT EXISTS Coupon (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    min_order_amount DECIMAL(10,2) DEFAULT 0,
    valid_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valid_until TIMESTAMP,
    max_uses INT DEFAULT 1,
    used_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 6. Customer Loyalty Table
-- =====================================================
CREATE TABLE IF NOT EXISTS CustomerLoyalty (
    loyalty_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    points INT DEFAULT 0,
    tier VARCHAR(20) DEFAULT 'BRONZE',
    total_spent DECIMAL(12,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- 7. Update Orders Table - Add missing columns
-- =====================================================
-- Run these one by one if IF NOT EXISTS doesn't work:
ALTER TABLE Orders ADD COLUMN cancelled_at TIMESTAMP NULL;
ALTER TABLE Orders ADD COLUMN cancel_reason TEXT;

-- If created_at column doesn't exist:
-- ALTER TABLE Orders ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- =====================================================
-- 8. Add discount_percent to Product table
-- =====================================================
ALTER TABLE Product ADD COLUMN IF NOT EXISTS discount_percent DOUBLE DEFAULT 0;

-- =====================================================
-- 9. Create indexes for better performance
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_orders_status ON Orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_carrier ON Orders(carrier_username);
CREATE INDEX IF NOT EXISTS idx_messages_receiver ON Messages(receiver_username, is_read);
CREATE INDEX IF NOT EXISTS idx_product_type ON Product(type);
CREATE INDEX IF NOT EXISTS idx_product_stock ON Product(stock);

-- =====================================================
-- 10. Insert default owner for messaging (if not exists)
-- =====================================================
INSERT IGNORE INTO UserInfo (username, password, role, address, phone)
VALUES ('owner', 'owner123', 'owner', 'Store Address', '+901234567890');

-- =====================================================
-- 11. System Messages Table for Owner Alerts
-- =====================================================
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
