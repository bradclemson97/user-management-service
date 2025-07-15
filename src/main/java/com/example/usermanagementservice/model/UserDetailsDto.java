package com.example.usermanagementservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The user details data transfer object.
 */
@Data
@Builder
@Jacksonized
public class UserDetailsDto {

    private UUID userDetailId;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String primaryEmail;
    private OffsetDateTime knownFromDate;
    private OffsetDateTime knownToDate;
}
