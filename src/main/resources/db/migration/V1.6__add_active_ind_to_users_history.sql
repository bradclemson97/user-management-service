/* V1.3 added active_ind to users but omitted users_history — this migration corrects that. */

ALTER TABLE user_management.users_history ADD COLUMN active_ind varchar(10);
