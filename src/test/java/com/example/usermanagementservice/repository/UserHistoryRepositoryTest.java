package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.model.UserAuditRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserHistoryRepository Unit Tests")
class UserHistoryRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserHistoryRepository repository;

    private final UUID systemUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repository = new UserHistoryRepository(jdbcTemplate);
    }

    @Test
    @DisplayName("findBySystemUserId - delegates to JdbcTemplate with correct parameter")
    void findBySystemUserId_delegatesToJdbcTemplate() {
        UserAuditRecord record = UserAuditRecord.builder()
                .validFrom(Instant.now())
                .lockedUserInd("NO")
                .activeInd("YES")
                .failedLoginAttempts(0)
                .build();

        given(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq(systemUserId)))
                .willReturn(List.of(record));

        List<UserAuditRecord> result = repository.findBySystemUserId(systemUserId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(record);
        verify(jdbcTemplate).query(any(String.class), any(RowMapper.class), eq(systemUserId));
    }

    @Test
    @DisplayName("findBySystemUserId - returns empty list when no history exists")
    void findBySystemUserId_returnsEmptyList() {
        given(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq(systemUserId)))
                .willReturn(List.of());

        List<UserAuditRecord> result = repository.findBySystemUserId(systemUserId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("RowMapper - maps ResultSet columns to UserAuditRecord correctly")
    @SuppressWarnings("unchecked")
    void rowMapper_mapsResultSet() throws Exception {
        Instant now = Instant.now();
        UUID modifiedBy = UUID.randomUUID();

        ResultSet rs = mock(ResultSet.class);
        given(rs.getTimestamp("valid_from")).willReturn(Timestamp.from(now));
        given(rs.getTimestamp("valid_to")).willReturn(null);
        given(rs.getString("locked_user_ind")).willReturn("YES");
        given(rs.getString("active_ind")).willReturn("NO");
        given(rs.getInt("failed_login_attempts")).willReturn(3);
        given(rs.getString("modified_by")).willReturn(modifiedBy.toString());
        given(rs.getString("modified_by_name")).willReturn("Jane Smith");
        given(rs.getTimestamp("modified_date")).willReturn(Timestamp.from(now));

        ArgumentCaptor<RowMapper<UserAuditRecord>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);
        given(jdbcTemplate.query(any(String.class), mapperCaptor.capture(), eq(systemUserId)))
                .willReturn(List.of());

        repository.findBySystemUserId(systemUserId);

        UserAuditRecord mapped = mapperCaptor.getValue().mapRow(rs, 0);

        assertThat(mapped).isNotNull();
        assertThat(mapped.getValidFrom()).isEqualTo(now);
        assertThat(mapped.getValidTo()).isNull();
        assertThat(mapped.getLockedUserInd()).isEqualTo("YES");
        assertThat(mapped.getActiveInd()).isEqualTo("NO");
        assertThat(mapped.getFailedLoginAttempts()).isEqualTo(3);
        assertThat(mapped.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(mapped.getModifiedByName()).isEqualTo("Jane Smith");
        assertThat(mapped.getModifiedDate()).isEqualTo(now);
    }
}
