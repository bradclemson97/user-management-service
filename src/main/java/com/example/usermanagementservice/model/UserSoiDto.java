package com.example.usermanagementservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * A model representing the information of a user. Used in UserAuditSoi for JpaAuditEntity.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSoiDto {

    private UUID systemUserId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String primaryEmail;
}
