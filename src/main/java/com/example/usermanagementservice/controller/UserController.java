package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.enums.UserSearchSort;
import com.example.usermanagementservice.exception.response.ApiError;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.usermanagementservice.config.SystemConstant.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * APIs for retrieving and managing Users.
 */
@Tag(name = "User Controller", description = "Controller for managing Users")
@ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@Validated
@RequestMapping(API_VERSION + API_USER)
public interface UserController {

    @Operation(summary = "Create new user", description = "Create a new user")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "User creation successful")
    CreateUserResponse createUser(
            @RequestBody @Valid CreateUserRequest userRequest);

    @Operation(summary = "Get user", description = "Get user information for the provided systemUserId")
    @GetMapping("{systemUserId}")
    UserDto getUser(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
            description = "The user's unique reference")
            @PathVariable UUID systemUserId);

    @Operation(summary = "Get user by primary email", description = "Get user information for the provided primaryEmail")
    @GetMapping(API_EMAIL + "/" + "{primaryEmailId}")
    UserDto getUserByPrimaryEmail(
            @Parameter(example = "test@test.com",
                    description = "The user's primary email")
            @PathVariable String primaryEmail);

    @Operation(summary = "Get current user",
            description = "Get current user information via session token claims")
    @GetMapping(API_CURRENT)
    UserDto getCurrentUser(
            @NotNull(message = "Session token is required")
            @AuthenticationPrincipal(expression = "systemUserId")
            UUID systemUserId);

    @Operation(summary = "Get user SOI",
            description = "Get user SOI fir the provided systemUserId")
    @GetMapping(API_SOI + "/{systemUserId}")
    UserSoiDto getUserSoi(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId);

    @Operation(summary = "search for users",
    description = "seach users with the provided request and return a user dto")
    @GetMapping
    Page<UserDto> userSearch(
            @Size(min = 2)
            @Parameter(description = "any combination of a user's firstName, middleName or lastName",
            example = "John Ola Smith")
            @RequestParam String name,
            @Parameter(description = "sorting criteria as enum", example = "NAME")
            @RequestParam(name = "sortBy", required = false, defaultValue = "NAME")
            UserSearchSort sortBy,
            @Parameter(description = "sorting direction", example = "ASC")
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC")
            Sort.Direction direction,
            @PositiveOrZero(message = "The page number must be at least 0")
            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0")
            int page,
            @Positive(message = "The page size must be at least 1")
            @Parameter(description = "Page size for pagination", example = "25")
            @RequestParam(name = "pageSize", required = false, defaultValue = "25")
            int size);

}
