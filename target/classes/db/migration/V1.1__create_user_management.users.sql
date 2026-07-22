SET search_path TO user_management;

CREATE TABLE users (
  usr_id BIGSERIAL NOT NULL,
  system_user_id UUID NOT NULL UNIQUE,
  locked_user_ind varchar(10) NOT NULL,
  sys_period tstzrange NOT NULL DEFAULT tstzrange(current_timestamp, null),
  created_by UUID NOT NULL,
  created_date timestamp with time zone NOT NULL,
  modified_by UUID NOT NULL,
  modified_date timestamp with time zone NOT NULL
);

CREATE TABLE users_history (LIKE users);
ALTER TABLE users_history ADD COLUMN hist_id BIGSERIAL NOT NULL;

CREATE TRIGGER versioning_trigger_users
  BEFORE INSERT OR UPDATE OR DELETE ON users
  FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'user_management.users_history', true);

ALTER TABLE users ADD CONSTRAINT usr_pk PRIMARY KEY (usr_id);

CREATE INDEX system_user_id_index ON users (system_user_id);
