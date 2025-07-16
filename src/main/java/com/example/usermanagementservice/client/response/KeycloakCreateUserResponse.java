package com.example.usermanagementservice.client.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * A response to create a user in Keycloak.
 */
@Data
@Builder
@Jacksonized
public class KeycloakCreateUserResponse {

    private UUID systemUserId;
    private String password;
}
