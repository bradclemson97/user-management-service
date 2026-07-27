package com.example.usermanagementservice.service;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.request.UpdateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
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
     * get the user information by  systemUserId.
     *
     * @param systemUserId the systemUserId of the user
     * @return the user information.
     */
    UserDto getUser(UUID systemUserId);

    /**
     * find the user details by primaryEmail.
     *
     * @param primaryEmail the primary email of the user details to find
     * @return the found user details.
     */
    UserDetails findUserDetailsByPrimaryEmail(String primaryEmail);

    /**
     * get the user information by primaryEmail.
     * This is to be used by keycloak-manager to
     * retrieve account details for a user.
     *
     * @param primaryEmail the primary email of the user
     * @return the user information.
     */
    UserDto getUserByPrimaryEmail(String primaryEmail);

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

    /**
     * Update user profile by creating a new temporal UserDetails record and closing the current one.
     *
     * @param systemUserId the systemUserId of the user to update
     * @param request the fields to update
     * @return the updated user.
     */
    UserDto updateUser(UUID systemUserId, UpdateUserRequest request);

    /**
     * Deactivate a user by setting active = NO and closing their current UserDetails record.
     *
     * @param systemUserId the systemUserId of the user to deactivate
     */
    void deactivateUser(UUID systemUserId);

    /**
     * Record a successful login for the user by updating their last_login_date to now.
     *
     * @param systemUserId the systemUserId of the user who logged in
     */
    void recordLogin(UUID systemUserId);

    /**
     * Lock a user account after too many failed login attempts.
     *
     * @param systemUserId the systemUserId of the user to lock
     * @param failedAttempts the current failed attempt count from Keycloak
     */
    void lockUser(UUID systemUserId, int failedAttempts);

    /**
     * Unlock a user account and clear their failed login attempt counter.
     *
     * @param systemUserId the systemUserId of the user to unlock
     */
    void unlockUser(UUID systemUserId);
}
