package com.example.usermanagementservice.service;

import com.example.usermanagementservice.client.KeycloakManagerClient;
import com.example.usermanagementservice.client.request.KeycloakCreateUserRequest;
import com.example.usermanagementservice.client.response.KeycloakCreateUserResponse;
import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.exception.ConflictException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import com.example.usermanagementservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of User Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakManagerClient keycloakManagerClient;

    /**
     * Creates a user with the specified fields.
     * Calls the Keycloak manager which uses UserRepresentation and
     * CredentialRepresentation to create password credential.
     *
     * @param request the CreateUserRequest with the fields for the new user
     * @return CreateUserResponse
     */
    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsBySystemUserId(request.getSystemUserId())) {
            throw new ConflictException("SystemUserId already exists");
        }

            User user = userMapper.requestToUser(request);
            user = userRepository.save(user);
            UUID systemUserId = user.getSystemUserId();
            log.info("User {} saved in User Manager Transactionaly", user);

            String password = null;
            KeycloakCreateUserRequest keycloakRequest = userMapper.requestToKeycloak(request);
            KeycloakCreateUserResponse keycloakResponse = keycloakManagerClient.createUser(keycloakRequest);
            password = keycloakResponse.getPassword();

        return CreateUserResponse.builder()
                    .systemUserId(systemUserId)
                    .password(password)
                    .build();
    }

    @Override
    public User findUser(UUID systemUserId) {
        return userRepository.findBySystemUserId(systemUserId)
                .orElseThrow(() -> {
                    log.info("User not found for systemUserId {}", systemUserId);
                    return new EntityNotFoundException("User not found for systemUserId");
                });
    }

    @Override
    public UserDto getUser(UUID systemUserId) {
        User user = findUser(systemUserId);
        return userMapper.userToDto(user);
    }

    @Override
    public UserSoiDto getUserSoi(UUID systemUserId) {
        User user = findUser(systemUserId);
        return userMapper.userToUserSoi(user);
    }
}
