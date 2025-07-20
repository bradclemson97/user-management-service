package com.example.usermanagementservice.client.model;

import com.example.usermanagementservice.domain.enums.YesNo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.usermanagementservice.domain.enums.YesNo.NA;

/**
 * Represents a Data Transfer Object (DTO) for a user.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcmUserDto {

    private UUID systemUserId;
    @Builder.Default
    private YesNo locked = NA;
    @Builder.Default
    private Set<RoleDto> permissions = new HashSet<>();
}
