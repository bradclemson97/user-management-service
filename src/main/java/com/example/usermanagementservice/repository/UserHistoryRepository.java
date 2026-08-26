package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.model.UserAuditRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<UserAuditRecord> findBySystemUserId(UUID systemUserId) {
        return jdbcTemplate.query("""
                SELECT
                    lower(h.sys_period)    AS valid_from,
                    upper(h.sys_period)    AS valid_to,
                    h.locked_user_ind,
                    h.active_ind,
                    h.failed_login_attempts,
                    h.modified_by,
                    h.modified_date,
                    COALESCE(ud.first_name || ' ' || ud.last_name, h.modified_by::text) AS modified_by_name
                FROM user_management.users_history h
                LEFT JOIN user_management.users mu ON mu.system_user_id = h.modified_by
                LEFT JOIN user_management.user_details ud ON ud.usr_id = mu.usr_id
                    AND ud.known_to_date IS NULL
                WHERE h.system_user_id = ?
                ORDER BY lower(h.sys_period) DESC
                """,
                (rs, i) -> {
                    Timestamp validTo = rs.getTimestamp("valid_to");
                    String modifiedByStr = rs.getString("modified_by");
                    Timestamp modifiedDate = rs.getTimestamp("modified_date");
                    return UserAuditRecord.builder()
                            .validFrom(rs.getTimestamp("valid_from").toInstant())
                            .validTo(validTo != null ? validTo.toInstant() : null)
                            .lockedUserInd(rs.getString("locked_user_ind"))
                            .activeInd(rs.getString("active_ind"))
                            .failedLoginAttempts(rs.getInt("failed_login_attempts"))
                            .modifiedBy(modifiedByStr != null ? UUID.fromString(modifiedByStr) : null)
                            .modifiedByName(rs.getString("modified_by_name"))
                            .modifiedDate(modifiedDate != null ? modifiedDate.toInstant() : null)
                            .build();
                },
                systemUserId);
    }
}
