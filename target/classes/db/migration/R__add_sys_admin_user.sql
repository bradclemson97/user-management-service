DO
$$
    DECLARE
        userId BIGINT;
    BEGIN

        INSERT INTO user_management.users (system_user_id, locked_user_ind, active_ind, created_by, created_date, modified_by, modified_date)
        VALUES (
            '${system-admin-systemuser-id}'::UUID,
            'NO',
            'YES',
            '${system-admin-systemuser-id}'::UUID,
            now(),
            '${system-admin-systemuser-id}'::UUID,
            now()
        )
        ON CONFLICT (system_user_id)
            DO UPDATE SET modified_by = EXCLUDED.modified_by, modified_date = EXCLUDED.modified_date
        RETURNING usr_id INTO userId;

        IF NOT EXISTS (SELECT 1 FROM user_management.user_details WHERE usr_id = userId) THEN
            INSERT INTO user_management.user_details (usr_id, first_name, last_name, primary_email, known_from_date, created_by, created_date, modified_by, modified_date)
            VALUES (
                userId,
                '${system-admin-firstname}',
                '${system-admin-lastname}',
                '${system-admin-email}',
                now(),
                '${system-admin-systemuser-id}'::UUID,
                now(),
                '${system-admin-systemuser-id}'::UUID,
                now()
            );
        END IF;

    END
$$;
