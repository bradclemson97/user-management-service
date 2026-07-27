package com.example.usermanagementservice.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakLockoutStatusResponse {
    private boolean lockedByKeycloak;
    private int failedAttempts;
}
