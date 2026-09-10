CREATE TABLE categories (
 id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(80) NOT NULL, slug VARCHAR(100) NOT NULL, description VARCHAR(500), image_url VARCHAR(500), active BOOLEAN NOT NULL DEFAULT TRUE, display_order INT NOT NULL,
 PRIMARY KEY (id), UNIQUE KEY uk_categories_name (name), UNIQUE KEY uk_categories_slug (slug)
) ENGINE=InnoDB;
CREATE TABLE products (
 id BIGINT NOT NULL AUTO_INCREMENT, version BIGINT NOT NULL DEFAULT 0, sku VARCHAR(64) NOT NULL, name VARCHAR(180) NOT NULL, description VARCHAR(2000), brand VARCHAR(100), mrp DECIMAL(12,2) NOT NULL, selling_price DECIMAL(12,2) NOT NULL, discount_percentage DECIMAL(5,2) NOT NULL DEFAULT 0, unit VARCHAR(40) NOT NULL, weight VARCHAR(40), size VARCHAR(40), image_url VARCHAR(500), availability BOOLEAN NOT NULL DEFAULT TRUE, stock_quantity INT NOT NULL, delivery_type VARCHAR(50), category_id BIGINT NOT NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (id), UNIQUE KEY uk_products_sku (sku), KEY idx_products_category_availability (category_id,availability), KEY idx_products_name (name), CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB;
CREATE TABLE product_images (
 id BIGINT NOT NULL AUTO_INCREMENT, product_id BIGINT NOT NULL, image_url VARCHAR(500) NOT NULL, alt_text VARCHAR(180), display_order INT NOT NULL DEFAULT 0,
 PRIMARY KEY (id), KEY idx_product_images_product (product_id), CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE TABLE customers (
 id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(120) NOT NULL, phone VARCHAR(20) NOT NULL, email VARCHAR(160), created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (id), UNIQUE KEY uk_customers_phone (phone), UNIQUE KEY uk_customers_email (email)
) ENGINE=InnoDB;
CREATE TABLE addresses (
 id BIGINT NOT NULL AUTO_INCREMENT, customer_id BIGINT NOT NULL, label VARCHAR(30) NOT NULL, recipient_name VARCHAR(120) NOT NULL, phone VARCHAR(20) NOT NULL, line1 VARCHAR(250) NOT NULL, line2 VARCHAR(250), landmark VARCHAR(150), city VARCHAR(80) NOT NULL, state VARCHAR(80) NOT NULL, postal_code VARCHAR(12) NOT NULL, latitude DECIMAL(10,7), longitude DECIMAL(10,7), default_address BOOLEAN NOT NULL DEFAULT FALSE,
 PRIMARY KEY (id), KEY idx_addresses_customer (customer_id), CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;
CREATE TABLE orders (
 id BIGINT NOT NULL AUTO_INCREMENT, order_number VARCHAR(40) NOT NULL, customer_id BIGINT NOT NULL, status VARCHAR(30) NOT NULL, payment_method VARCHAR(20) NOT NULL, payment_status VARCHAR(20) NOT NULL, subtotal DECIMAL(12,2) NOT NULL, delivery_fee DECIMAL(12,2) NOT NULL DEFAULT 0, discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0, total_amount DECIMAL(12,2) NOT NULL, address_snapshot VARCHAR(1000) NOT NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (id), UNIQUE KEY uk_orders_number (order_number), KEY idx_orders_customer_created (customer_id,created_at), CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;
CREATE TABLE order_items (
 id BIGINT NOT NULL AUTO_INCREMENT, order_id BIGINT NOT NULL, product_id BIGINT NOT NULL, product_sku VARCHAR(64) NOT NULL, product_name VARCHAR(180) NOT NULL, product_brand VARCHAR(100), product_image_url VARCHAR(500), product_unit VARCHAR(40), quantity INT NOT NULL, unit_price DECIMAL(12,2) NOT NULL, line_total DECIMAL(12,2) NOT NULL,
 PRIMARY KEY (id), KEY idx_order_items_order (order_id), CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;
CREATE TABLE partner_inventory (
 id BIGINT NOT NULL AUTO_INCREMENT, partner_code VARCHAR(80) NOT NULL, partner_name VARCHAR(150) NOT NULL, product_id BIGINT NOT NULL, stock_quantity INT NOT NULL, partner_price DECIMAL(12,2), updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (id), UNIQUE KEY uk_partner_inventory_product (partner_code,product_id), CONSTRAINT fk_partner_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;
CREATE TABLE delivery_partners (
 id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(120) NOT NULL, phone VARCHAR(20) NOT NULL, vehicle_number VARCHAR(40), active BOOLEAN NOT NULL DEFAULT TRUE, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (id), UNIQUE KEY uk_delivery_partners_phone (phone)
) ENGINE=InnoDB;
CREATE TABLE order_assignments (
 id BIGINT NOT NULL AUTO_INCREMENT, order_id BIGINT NOT NULL, delivery_partner_id BIGINT NOT NULL, status VARCHAR(30) NOT NULL, assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, delivered_at DATETIME,
 PRIMARY KEY (id), UNIQUE KEY uk_order_assignments_order (order_id), KEY idx_assignments_partner_status (delivery_partner_id,status), CONSTRAINT fk_assignments_order FOREIGN KEY (order_id) REFERENCES orders(id), CONSTRAINT fk_assignments_partner FOREIGN KEY (delivery_partner_id) REFERENCES delivery_partners(id)
) ENGINE=InnoDB;
