package com.example.usermanagementservice.client.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * A request to create a user in Access Control Manager
 */
@Data
@Builder
@Jacksonized
public class AcmCreateUserRequest {

    private UUID systemUserId;
}
