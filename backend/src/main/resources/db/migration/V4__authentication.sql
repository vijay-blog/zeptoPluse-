CREATE TABLE user_accounts (
 id BIGINT NOT NULL AUTO_INCREMENT,
 name VARCHAR(120) NOT NULL,
 email VARCHAR(160) NOT NULL,
 password_hash VARCHAR(100) NOT NULL,
 role VARCHAR(30) NOT NULL,
 status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (id),
 UNIQUE KEY uk_user_accounts_email (email),
 KEY idx_user_accounts_role_status (role,status)
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
 id BIGINT NOT NULL AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 token_hash VARCHAR(128) NOT NULL,
 expires_at DATETIME NOT NULL,
 revoked BOOLEAN NOT NULL DEFAULT FALSE,
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (id),
 UNIQUE KEY uk_refresh_tokens_hash (token_hash),
 KEY idx_refresh_tokens_user (user_id),
 CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB;
