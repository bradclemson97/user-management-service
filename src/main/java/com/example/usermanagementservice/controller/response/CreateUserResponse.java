package com.example.usermanagementservice.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * The object for the data of create User Responses.
 */
@Data
@Builder
@Jacksonized
public class CreateUserResponse {
    @NotNull(message = "The field systemUserId is required")
    @Schema(description = "The system user ID of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID systemUserId;

    @Schema(description = "The password for the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;

}
