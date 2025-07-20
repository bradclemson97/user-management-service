DO
$$
    DECLARE
        userId INTEGER;
    BEGIN

        INSERT INTO user_management.users (system_user_id, locked_user_ind, created_by, created_date, modified_by, modified_date)
        VALUES ('${system-admin-systemuser-id}', 'NO', '9a908a6d-77hq-38sj-28ka-9aa90tg9h56f', now(), '9a908a6d-77hq-38sj-28ka-9aa90tg9h56f', now())
        ON CONFLICT (system_user_id)
            DO UPDATE SET modified_by = EXCLUDED.modified_by, modified_date = EXCLUDED.modified_date
        RETURNING user_id INTO userId;

        INSERT INTO user_management.user_details (usr_id, first_name, last_name, primary_email, known_from_date,created_by, created_date, modified_by, modified_date)
        VALUES (userId, '${system-admin-firstname}', '${system-admin-lastname}', '${system-admin-email}', now(), '9a908a6d-77hq-38sj-28ka-9aa90tg9h56f', now(), '9a908a6d-77hq-38sj-28ka-9aa90tg9h56f', now());

    END
$$;