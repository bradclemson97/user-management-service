package com.example.usermanagementservice.client.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * A request to create a user in Keycloak.
 */
@Data
@Builder
@Jacksonized
public class KeycloakCreateUserRequest {

    private UUID systemUserId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
}
