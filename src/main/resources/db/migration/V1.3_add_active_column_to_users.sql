/* Add active_ind column to users table — defaults to 'Yes' for existing rows */

ALTER TABLE user_management.users ADD COLUMN active_ind varchar(10) NOT NULL DEFAULT 'Yes';
