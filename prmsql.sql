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

-- ... (remaining tables and inserts as provided above)

INSERT IGNORE INTO role (id, name)
VALUES (UUID_TO_BIN(UUID()), 'ROLE_MANAGER'),
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
