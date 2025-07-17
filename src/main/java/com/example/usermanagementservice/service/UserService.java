package com.example.usermanagementservice.service;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.enums.UserSearchSort;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

/**
 * Service layer of retrieving and managing Users.
 */
public interface UserService {

    /**
     * Creates a new user with the specified fields.
     *
     * @param request the DTO with the fields for the new user
     * @return the generated saga response.
     */
    CreateUserResponse createUser(CreateUserRequest request);

    /**
     * find the user by systemUserId.
     *
     * @param systemUserId the systemUserId of the user to find
     * @return the found user.
     */
    User findUser(UUID systemUserId);

    /**
     * get the user information by systemUserId.
     *
     * @param systemUserId the systemUserId of the user
     * @return the user information.
     */
    UserDto getUser(UUID systemUserId);

    /**
     * get the user SOI by systemUserId.
     *
     * @param systemUserId the systemUserId of the user
     * @return the user SOI.
     */
    UserSoiDto getUserSoi(UUID systemUserId);

    /**
     * Search for user by given criteria in request.
     *
     * @param name the user's name
     * @param sortBy field to sort by
     * @param sortDirection direction to sort users
     * @param page page number of users to be returned
     * @param size size of the page to be returned
     * @return UserDto
     */
    Page<UserDto> search(String name, UserSearchSort sortBy, Sort.Direction sortDirection,
                         int page, int size);
}
