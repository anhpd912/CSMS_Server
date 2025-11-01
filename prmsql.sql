-- ======================================
--  COFFEESHOP_DB – UUID in BINARY(16)
-- ======================================

CREATE DATABASE IF NOT EXISTS coffeeshop_db
    CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

USE coffeeshop_db;

-- 1. Role
CREATE TABLE IF NOT EXISTS role (
                                    id BINARY(16) NOT NULL,
                                    name VARCHAR(50) NOT NULL UNIQUE,
                                    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 2. Loyalty
CREATE TABLE IF NOT EXISTS loyalty (
                                       loyalty_id BINARY(16) NOT NULL,
                                       points_balance INT NOT NULL DEFAULT 0,
                                       PRIMARY KEY (loyalty_id)
) ENGINE=InnoDB;

-- 3. User
CREATE TABLE IF NOT EXISTS user (
                                    id BINARY(16) NOT NULL,
                                    email VARCHAR(100) NOT NULL UNIQUE,
                                    password VARCHAR(255) NOT NULL,
                                    fullname VARCHAR(100) NOT NULL,
                                    mobile VARCHAR(20) NOT NULL,
                                    role_id BINARY(16) NOT NULL,
                                    loyalty_id BINARY(16),
                                    PRIMARY KEY (id),
                                    FOREIGN KEY (role_id) REFERENCES role(id),
                                    FOREIGN KEY (loyalty_id) REFERENCES loyalty(loyalty_id)
) ENGINE=InnoDB;

-- 4. Customer
CREATE TABLE IF NOT EXISTS customer (
                                        id BINARY(16) NOT NULL,
                                        full_name VARCHAR(100),
                                        phone VARCHAR(20) NOT NULL UNIQUE,
                                        email VARCHAR(100),
                                        loyalty_id BINARY(16),
                                        PRIMARY KEY (id),
                                        FOREIGN KEY (loyalty_id) REFERENCES loyalty(loyalty_id)
) ENGINE=InnoDB;

-- 5. TableInfo
CREATE TABLE IF NOT EXISTS table_info (
                                          id BINARY(16) NOT NULL,
                                          name VARCHAR(20) NOT NULL UNIQUE,
                                          location VARCHAR(50),
                                          seat_count INT,
                                          status VARCHAR(20) NOT NULL,
                                          PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 6. Category
CREATE TABLE IF NOT EXISTS category (
                                        id BINARY(16) NOT NULL,
                                        name VARCHAR(50) NOT NULL UNIQUE,
                                        PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 7. Product
CREATE TABLE IF NOT EXISTS product (
                                       id BINARY(16) NOT NULL,
                                       name VARCHAR(100) NOT NULL,
                                       description TEXT,
                                       price DECIMAL(10, 2) NOT NULL,
                                       category_id BINARY(16) NOT NULL,
                                       image_link VARCHAR(255),
                                       status VARCHAR(20) NOT NULL,
                                       PRIMARY KEY (id),
                                       FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB;

-- 8. Menu
CREATE TABLE IF NOT EXISTS menu (
                                    id BINARY(16) NOT NULL,
                                    name VARCHAR(50) NOT NULL UNIQUE,
                                    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 9. Menu_Product
CREATE TABLE IF NOT EXISTS menu_product (
                                            id BINARY(16) NOT NULL,
                                            menu_id BINARY(16) NOT NULL,
                                            product_id BINARY(16) NOT NULL,
                                            PRIMARY KEY (id),
                                            FOREIGN KEY (menu_id) REFERENCES menu(id),
                                            FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

-- 10. Order
CREATE TABLE IF NOT EXISTS `order` (
                                       id BINARY(16) NOT NULL,
                                       order_date DATETIME NOT NULL,
                                       total_price DECIMAL(10, 2),
                                       status VARCHAR(50) NOT NULL,
                                       customer_id BINARY(16),
                                       staff_id BINARY(16) NOT NULL,
                                       PRIMARY KEY (id),
                                       FOREIGN KEY (customer_id) REFERENCES customer(id),
                                       FOREIGN KEY (staff_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 11. Order_Detail
CREATE TABLE IF NOT EXISTS order_detail (
                                            id BINARY(16) NOT NULL,
                                            order_id BINARY(16) NOT NULL,
                                            product_id BINARY(16) NOT NULL,
                                            quantity INT NOT NULL,
                                            price DECIMAL(10, 2) NOT NULL,
                                            PRIMARY KEY (id),
                                            FOREIGN KEY (order_id) REFERENCES `order`(id),
                                            FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

-- 12. Payment
CREATE TABLE IF NOT EXISTS payment (
                                       id BINARY(16) NOT NULL,
                                       order_id BINARY(16) NOT NULL,
                                       amount DECIMAL(10, 2) NOT NULL,
                                       method VARCHAR(20) NOT NULL,
                                       payment_time DATETIME NOT NULL,
                                       PRIMARY KEY (id),
                                       FOREIGN KEY (order_id) REFERENCES `order`(id)
) ENGINE=InnoDB;

-- 13. Bill
CREATE TABLE IF NOT EXISTS bill (
                                    id BINARY(16) NOT NULL,
                                    order_id BINARY(16) NOT NULL,
                                    payment_id BINARY(16) NOT NULL,
                                    discount DECIMAL(10, 2),
                                    tax DECIMAL(10, 2),
                                    final_amount DECIMAL(10, 2) NOT NULL,
                                    issued_time DATETIME NOT NULL,
                                    payment_status VARCHAR(50) NOT NULL,
                                    PRIMARY KEY (id),
                                    FOREIGN KEY (order_id) REFERENCES `order`(id),
                                    FOREIGN KEY (payment_id) REFERENCES payment(id)
) ENGINE=InnoDB;

-- 14. Loyalty_Transaction
CREATE TABLE IF NOT EXISTS loyalty_transaction (
                                                   id BINARY(16) NOT NULL,
                                                   loyalty_id BINARY(16) NOT NULL,
                                                   order_id BINARY(16) NOT NULL,
                                                   points_earned INT,
                                                   points_spent INT,
                                                   timestamp DATETIME NOT NULL,
                                                   PRIMARY KEY (id),
                                                   FOREIGN KEY (loyalty_id) REFERENCES loyalty(loyalty_id),
                                                   FOREIGN KEY (order_id) REFERENCES `order`(id)
) ENGINE=InnoDB;

-- 15. Reservation
CREATE TABLE IF NOT EXISTS reservation (
                                           reservation_id BINARY(16) NOT NULL,
                                           table_id BINARY(16) NOT NULL,
                                           customer_id BINARY(16),
                                           booked_by_user_id BINARY(16),
                                           reserve_time DATETIME NOT NULL,
                                           num_guests INT,
                                           status VARCHAR(50) NOT NULL,
                                           PRIMARY KEY (reservation_id),
                                           FOREIGN KEY (table_id) REFERENCES table_info(id),
                                           FOREIGN KEY (customer_id) REFERENCES customer(id),
                                           FOREIGN KEY (booked_by_user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 16. Shift
CREATE TABLE IF NOT EXISTS shift (
                                     shift_id BINARY(16) NOT NULL,
                                     user_id BINARY(16) NOT NULL,
                                     start_time DATETIME NOT NULL,
                                     end_time DATETIME,
                                     opening_cash DECIMAL(10, 2),
                                     closing_cash DECIMAL(10, 2),
                                     status VARCHAR(50) NOT NULL,
                                     PRIMARY KEY (shift_id),
                                     FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 17. Attendance
CREATE TABLE IF NOT EXISTS attendance (
                                          attendance_id BINARY(16) NOT NULL,
                                          user_id BINARY(16) NOT NULL,
                                          clock_in_time DATETIME NOT NULL,
                                          clock_out_time DATETIME,
                                          method VARCHAR(50) NOT NULL,
                                          PRIMARY KEY (attendance_id),
                                          FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 18. Staff_Evaluation
CREATE TABLE IF NOT EXISTS staff_evaluation (
                                                evaluation_id BINARY(16) NOT NULL,
                                                staff_id BINARY(16) NOT NULL,
                                                shift_id BINARY(16) NOT NULL,
                                                rating INT,
                                                comments TEXT,
                                                evaluation_date DATETIME NOT NULL,
                                                PRIMARY KEY (evaluation_id),
                                                FOREIGN KEY (staff_id) REFERENCES user(id),
                                                FOREIGN KEY (shift_id) REFERENCES shift(shift_id)
) ENGINE=InnoDB;

-- 19. Stock
CREATE TABLE IF NOT EXISTS stock (
                                     product_id BINARY(16) NOT NULL,
                                     quantity_in_stock INT NOT NULL,
                                     reorder_level INT,
                                     PRIMARY KEY (product_id),
                                     FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

-- 20. Inventory_Transaction
CREATE TABLE IF NOT EXISTS inventory_transaction (
                                                     transaction_id BINARY(16) NOT NULL,
                                                     product_id BINARY(16) NOT NULL,
                                                     quantity INT NOT NULL,
                                                     transaction_type VARCHAR(50) NOT NULL,
                                                     transaction_time DATETIME NOT NULL,
                                                     user_id BINARY(16) NOT NULL,
                                                     PRIMARY KEY (transaction_id),
                                                     FOREIGN KEY (product_id) REFERENCES product(id),
                                                     FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 21. Supplier
CREATE TABLE IF NOT EXISTS supplier (
                                        supplier_id BINARY(16) NOT NULL,
                                        name VARCHAR(100) NOT NULL,
                                        contact_person VARCHAR(100),
                                        phone VARCHAR(20),
                                        address VARCHAR(255),
                                        PRIMARY KEY (supplier_id)
) ENGINE=InnoDB;

-- 22. Purchase_Order
CREATE TABLE IF NOT EXISTS purchase_order (
                                              purchase_order_id BINARY(16) NOT NULL,
                                              supplier_id BINARY(16) NOT NULL,
                                              order_date DATETIME NOT NULL,
                                              total_amount DECIMAL(10, 2),
                                              status VARCHAR(50) NOT NULL,
                                              PRIMARY KEY (purchase_order_id),
                                              FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
) ENGINE=InnoDB;

-- 23. Purchase_Order_Detail
CREATE TABLE IF NOT EXISTS purchase_order_detail (
                                                     id BINARY(16) NOT NULL,
                                                     purchase_order_id BINARY(16) NOT NULL,
                                                     product_id BINARY(16) NOT NULL,
                                                     quantity INT NOT NULL,
                                                     cost_price DECIMAL(10, 2),
                                                     PRIMARY KEY (id),
                                                     FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(purchase_order_id),
                                                     FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

-- 24. Payroll_Deduction
CREATE TABLE IF NOT EXISTS payroll_deduction (
                                                 payroll_deduction_id BINARY(16) NOT NULL,
                                                 user_id BINARY(16) NOT NULL,
                                                 company_code VARCHAR(50),
                                                 employee_code VARCHAR(50),
                                                 from_date DATE NOT NULL,
                                                 description TEXT,
                                                 amount DECIMAL(10, 2),
                                                 PRIMARY KEY (payroll_deduction_id),
                                                 FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 25. Voucher
CREATE TABLE IF NOT EXISTS voucher (
                                       voucher_id BINARY(16) NOT NULL,
                                       code VARCHAR(50) NOT NULL UNIQUE,
                                       discount_type VARCHAR(50) NOT NULL,
                                       discount_value DECIMAL(10, 2) NOT NULL,
                                       start_date DATE,
                                       end_date DATE,
                                       status VARCHAR(50) NOT NULL,
                                       PRIMARY KEY (voucher_id)
) ENGINE=InnoDB;

-- 26. Order_Voucher
CREATE TABLE IF NOT EXISTS order_voucher (
                                             id BINARY(16) NOT NULL,
                                             order_id BINARY(16) NOT NULL,
                                             voucher_id BINARY(16) NOT NULL,
                                             discount_value DECIMAL(10, 2) NOT NULL,
                                             PRIMARY KEY (id),
                                             FOREIGN KEY (order_id) REFERENCES `order`(id),
                                             FOREIGN KEY (voucher_id) REFERENCES voucher(voucher_id)
) ENGINE=InnoDB;

-- 27. Table_Order
CREATE TABLE IF NOT EXISTS table_order (
                                           id BINARY(16) NOT NULL,
                                           order_id BINARY(16) NOT NULL,
                                           table_id BINARY(16) NOT NULL,
                                           PRIMARY KEY (id),
                                           FOREIGN KEY (order_id) REFERENCES `order`(id),
                                           FOREIGN KEY (table_id) REFERENCES table_info(id)
) ENGINE=InnoDB;

-- ======================================
-- 🔽 INSERT DỮ LIỆU KHỞI TẠO
-- ======================================

INSERT IGNORE INTO role (id, name) VALUES
                                       (UUID_TO_BIN(UUID()), 'ROLE_MANAGER'),
                                       (UUID_TO_BIN(UUID()), 'ROLE_CASHIER'),
                                       (UUID_TO_BIN(UUID()), 'ROLE_WAITER'),
                                       (UUID_TO_BIN(UUID()), 'ROLE_CUSTOMER');

INSERT IGNORE INTO user (id, email, password, fullname, mobile, role_id, loyalty_id)
VALUES (
           UUID_TO_BIN(UUID()),
           'manager@coffeeshop.com',
           '$2a$10$j.Avd.1.d9vS7pA5k.E05uFX.p8.v6.1C.V.kS.O.q.G8b.a.k.Z6',
           'Default Manager',
           '0123456789',
           (SELECT id FROM role WHERE name = 'ROLE_MANAGER'),
           NULL
       );
