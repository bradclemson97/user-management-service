package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.enums.UserSearchSort;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import com.example.usermanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Implementation of APIs for retrieving and managing users.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.info("Attempting to create user {} {} with systemUserId of {}",
                request.getFirstName(), request.getLastName(), request.getSystemUserId());
        CreateUserResponse response = userService.createUser(request);
        log.info("Successfully created user {} {} with systemUserId of {}",
                request.getFirstName(), request.getLastName(), request.getSystemUserId());
        return response;
    }

    @Override
    public UserDto getUser(UUID systemUserId) {
        log.info("Attempting to retrieve the user {}",
                systemUserId);
        UserDto user = userService.getUser(systemUserId);
        log.info("Successfully retrieved the user {}",
                systemUserId);
        return user;
    }

    /**
     * getUserByPrimaryEmail to be used by keycloak-manager to
     * retrieve account details for a user.
     *
     * @param primaryEmail the primary email of the user
     * @return user
     */
    @Override
    public UserDto getUserByPrimaryEmail(String primaryEmail) {
        log.info("Attempting to retrieve the user with email {}",
                primaryEmail);
        UserDto user = userService.getUserByPrimaryEmail(primaryEmail);
        log.info("Successfully retrieved the user with email {}",
                primaryEmail);
        return user;
    }

    @Override
    public UserDto getCurrentUser(UUID systemUserId) {
        log.info("Attempting to retrieve the current user {}",
                systemUserId);
        UserDto user = userService.getUser(systemUserId);
        log.info("Successfully retrieved the current user {}",
                systemUserId);
        return user;
    }

    @Override
    public UserSoiDto getUserSoi(UUID systemUserId) {
        log.info("Attempting to retrieve the SOI for user {}",
                systemUserId);
        UserSoiDto user = userService.getUserSoi(systemUserId);
        log.info("Successfully retrieved the SOI for user {}",
                systemUserId);
        return user;
    }

    @Override
    public Page<UserDto> userSearch(String name, UserSearchSort sort, Sort.Direction direction,
                                    int page, int size) {
        log.info("attempting to search for user with name {}, page {}, size {}, sort {}, direction {}",
                name, page, size, sort, direction);
        Page<UserDto> results = userService.search(name, sort, direction, page, size);
        log.info("successfully searched for user with name {}, page {}, size {}, sort {}, direction {}",
                name, page, size, sort, direction);
        return results;
    }
}
