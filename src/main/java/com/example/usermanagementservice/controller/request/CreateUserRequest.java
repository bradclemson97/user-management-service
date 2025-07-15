package com.example.usermanagementservice.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@Jacksonized
public class CreateUserRequest {

    @Schema(description = "The user ID of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    @Builder.Default
    private UUID systemUserId = UUID.randomUUID();

    @Schema(description = "The first name of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "The field firstName is required")
    private String firstName;

    @Schema(description = "The middle name of the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String middleName;

    @Schema(description = "The last name of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "The field lastName is required")
    private String lastName;

    @Schema(description = "The email address of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "The field email is required")
    @Email(regexp = ".+@.+\\..+")
    private String email;

}
