SET search_path TO user_management;

CREATE TABLE user_details (
  udl_id BIGSERIAL NOT NULL,
  usr_id BIGINT NOT NULL,
  user_detail_id UUID NOT NULL DEFAULT gen_random_uuid(),
  title varchar(10) NULL,
  first_name varchar(100) NOT NULL,
  middle_name varchar(100) NULL,
  last_name varchar(100) NOT NULL,
  primary_email varchar(1000) NOT NULL,
  known_from_date timestamp with time zone NOT NULL,
  known_to_date timestamp with time zone NULL,
  sys_period tstzrange NOT NULL DEFAULT tstzrange(current_timestamp, null),
  created_by UUID NOT NULL,
  created_date timestamp with time zone NOT NULL,
  modified_by UUID NOT NULL,
  modified_date timestamp with time zone NOT NULL
);

CREATE TABLE user_details_history (LIKE user_details);
ALTER TABLE user_details_history ADD COLUMN hist_id BIGSERIAL NOT NULL;

CREATE TRIGGER versioning_trigger_persons
  BEFORE INSERT OR UPDATE OR DELETE ON user_details
  FOR EACH ROW EXECUTE PROCEDURE versioning('sys_period', 'user_management.user_details_history', true);

ALTER TABLE user_details ADD CONSTRAINT udl_pk PRIMARY KEY (udl_id);

CREATE INDEX IXFK_udl_usr_fk ON user_details (usr_id ASC);

CREATE INDEX user_detail_id_index ON user_details (user_detail_id);

ALTER TABLE user_details ADD CONSTRAINT udl_usr_fk
  FOREIGN KEY (usr_id) REFERENCES users (usr_id) ON DELETE NO ACTION ON UPDATE NO ACTION;
