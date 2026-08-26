package com.example.usermanagementservice.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class UserAuditRecord {
    Instant validFrom;
    Instant validTo;
    String lockedUserInd;
    String activeInd;
    Integer failedLoginAttempts;
    UUID modifiedBy;
    String modifiedByName;
    Instant modifiedDate;
}
