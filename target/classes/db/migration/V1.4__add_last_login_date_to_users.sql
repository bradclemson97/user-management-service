ALTER TABLE user_management.users ADD COLUMN last_login_date timestamp with time zone;
ALTER TABLE user_management.users_history ADD COLUMN last_login_date timestamp with time zone;
