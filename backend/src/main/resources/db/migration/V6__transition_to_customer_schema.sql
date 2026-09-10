SET @rename_sql = (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'categories')
      AND NOT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'legacy_marketplace_categories'),
    'RENAME TABLE categories TO legacy_marketplace_categories',
    'SELECT 1'
  )
);
PREPARE rename_statement FROM @rename_sql;
EXECUTE rename_statement;
DEALLOCATE PREPARE rename_statement;

SET @rename_sql = (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'products')
      AND NOT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'legacy_marketplace_products'),
    'RENAME TABLE products TO legacy_marketplace_products',
    'SELECT 1'
  )
);
PREPARE rename_statement FROM @rename_sql;
EXECUTE rename_statement;
DEALLOCATE PREPARE rename_statement;

SET @rename_sql = (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'addresses')
      AND NOT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'legacy_marketplace_addresses'),
    'RENAME TABLE addresses TO legacy_marketplace_addresses',
    'SELECT 1'
  )
);
PREPARE rename_statement FROM @rename_sql;
EXECUTE rename_statement;
DEALLOCATE PREPARE rename_statement;

SET @rename_sql = (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'orders')
      AND NOT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'legacy_marketplace_orders'),
    'RENAME TABLE orders TO legacy_marketplace_orders',
    'SELECT 1'
  )
);
PREPARE rename_statement FROM @rename_sql;
EXECUTE rename_statement;
DEALLOCATE PREPARE rename_statement;

SET @rename_sql = (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'order_items')
      AND NOT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'legacy_marketplace_order_items'),
    'RENAME TABLE order_items TO legacy_marketplace_order_items',
    'SELECT 1'
  )
);
PREPARE rename_statement FROM @rename_sql;
EXECUTE rename_statement;
DEALLOCATE PREPARE rename_statement;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  username VARCHAR(100) UNIQUE,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(40),
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  last_active_at TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL UNIQUE,
  description VARCHAR(1000),
  image_url VARCHAR(1000),
  active BOOLEAN NOT NULL,
  sort_order INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(2000),
  category_id BIGINT NOT NULL,
  price DECIMAL(19,2) NOT NULL,
  discount DECIMAL(19,2),
  sku VARCHAR(100),
  unit VARCHAR(50),
  stock INT,
  available BOOLEAN NOT NULL,
  image_url VARCHAR(1000),
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS addresses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  label VARCHAR(100),
  recipient_name VARCHAR(150),
  phone VARCHAR(40),
  address_line VARCHAR(1000),
  city VARCHAR(100),
  state VARCHAR(100),
  postal_code VARCHAR(30),
  latitude DOUBLE,
  longitude DOUBLE,
  default_address BOOLEAN NOT NULL,
  CONSTRAINT fk_address_customer FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  delivery_partner_id BIGINT,
  status VARCHAR(40) NOT NULL,
  payment_method VARCHAR(30),
  payment_status VARCHAR(30),
  subtotal DECIMAL(19,2) NOT NULL,
  delivery_fee DECIMAL(19,2) NOT NULL,
  total DECIMAL(19,2) NOT NULL,
  currency_code VARCHAR(10) NOT NULL,
  cancellation_reason VARCHAR(1000),
  delivery_address_id BIGINT,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  accepted_at TIMESTAMP(6),
  picked_up_at TIMESTAMP(6),
  out_for_delivery_at TIMESTAMP(6),
  delivered_at TIMESTAMP(6),
  CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_order_partner FOREIGN KEY (delivery_partner_id) REFERENCES users(id),
  CONSTRAINT fk_order_address FOREIGN KEY (delivery_address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(255) NOT NULL,
  unit_price DECIMAL(19,2) NOT NULL,
  quantity INT NOT NULL,
  line_total DECIMAL(19,2) NOT NULL,
  CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS delivery_partner_profiles (
  user_id BIGINT PRIMARY KEY,
  verification_status VARCHAR(30) NOT NULL,
  vehicle_type VARCHAR(100),
  vehicle_number VARCHAR(100),
  license_reference VARCHAR(150),
  available BOOLEAN NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(255),
  message VARCHAR(1000),
  created_at TIMESTAMP(6) NOT NULL,
  read_flag BOOLEAN NOT NULL,
  type VARCHAR(40),
  order_id BIGINT,
  action_url VARCHAR(1000),
  CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS earnings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  partner_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  earned_at TIMESTAMP(6) NOT NULL,
  amount DECIMAL(19,2) NOT NULL,
  currency_code VARCHAR(10) NOT NULL,
  status VARCHAR(30) NOT NULL,
  description VARCHAR(500),
  CONSTRAINT fk_earning_partner FOREIGN KEY (partner_id) REFERENCES users(id),
  CONSTRAINT fk_earning_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
