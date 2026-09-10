ALTER TABLE orders ADD COLUMN idempotency_key VARCHAR(80) NULL;
ALTER TABLE orders ADD CONSTRAINT uk_orders_idempotency_key UNIQUE (idempotency_key);

CREATE TABLE payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  gateway VARCHAR(30) NOT NULL,
  gateway_order_id VARCHAR(120),
  gateway_payment_id VARCHAR(120),
  gateway_signature VARCHAR(300),
  amount DECIMAL(12,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  status VARCHAR(20) NOT NULL,
  failure_reason VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_payments_gateway_order (gateway_order_id),
  KEY idx_payments_order (order_id),
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;
