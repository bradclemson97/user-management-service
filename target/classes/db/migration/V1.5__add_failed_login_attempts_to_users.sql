ALTER TABLE user_management.users ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0;
ALTER TABLE user_management.users_history ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0;
