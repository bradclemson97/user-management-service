package com.example.usermanagementservice.model;

import com.example.usermanagementservice.domain.enums.YesNo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * The user data transfer object.
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private UUID systemUserId;
    private YesNo active;
    private YesNo locked;
    private int failedLoginAttempts;
    private UserDetailsDto userDetails;
}
