package com.example.usermanagementservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

/**
 * Model of SourceOfInformation for auditing. Used in JpaAuditEntity.
 */
@Data
@Builder
@Jacksonized
public class UserAuditSoi {

    private UserSoiDto user;
    private OffsetDateTime dateTime;
}
