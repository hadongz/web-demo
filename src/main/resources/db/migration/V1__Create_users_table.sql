CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_account_locked ON users(is_account_locked);
CREATE INDEX idx_users_email_verified ON users(is_email_verified);
CREATE INDEX idx_users_last_login ON users(last_login_at);

ALTER TABLE users 
    ADD CONSTRAINT chk_username_length CHECK (LENGTH(username) >= 4);

ALTER TABLE users 
    ADD CONSTRAINT chk_password_length CHECK (LENGTH(password) >= 6);

ALTER TABLE users 
    ADD CONSTRAINT chk_failed_attempts_positive CHECK (failed_login_attempts >= 0);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();