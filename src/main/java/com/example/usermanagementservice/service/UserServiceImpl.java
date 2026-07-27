package com.example.usermanagementservice.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.example.usermanagementservice.client.AcmClient;
import com.example.usermanagementservice.client.KeycloakManagerClient;
import com.example.usermanagementservice.client.request.AcmCreateUserRequest;
import com.example.usermanagementservice.client.request.KeycloakCreateUserRequest;
import com.example.usermanagementservice.client.response.KeycloakCreateUserResponse;
import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.request.UpdateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.domain.enums.UserSearchSort;
import com.example.usermanagementservice.domain.enums.YesNo;
import com.example.usermanagementservice.exception.ConflictException;
import com.example.usermanagementservice.exception.NotFoundException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import com.example.usermanagementservice.repository.UserDetailsRepository;
import com.example.usermanagementservice.repository.UserRepository;
import jakarta.persistence.criteria.Path;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Implementation of User Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserMapper userMapper;
    private final KeycloakManagerClient keycloakManagerClient;
    private final AcmClient acmClient;

    private static final String KEYWORD_DELIMITER = " ";

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

            KeycloakCreateUserRequest keycloakRequest = userMapper.requestToKeycloak(request);
            KeycloakCreateUserResponse keycloakResponse = keycloakManagerClient.createUser(keycloakRequest);
            String password = keycloakResponse.getPassword();

            // Try to create user in ACM, if fails (should never happen), and is Keycloak user, remove it
        try {
            AcmCreateUserRequest acmRequest = AcmCreateUserRequest.builder()
                    .systemUserId(request.getSystemUserId())
                    .build();
            acmClient.createUser(acmRequest);
        } catch (Exception exception) {
            log.error("Problem creating new user {} in ACM, attempting to rollback user in Keycloak", systemUserId);
            keycloakManagerClient.rollbackCreateUser(request.getEmail());
            log.info("Successfully rolled back user in Keycloak for {}", systemUserId);
            throw exception;
        }

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
                    return new NotFoundException("User not found for systemUserId " + systemUserId);
                });
    }

    @Override
    public UserDto getUser(UUID systemUserId) {
        User user = findUser(systemUserId);
        return userMapper.userToDto(user);
    }

    @Override
    public UserDetails findUserDetailsByPrimaryEmail(String primaryEmail) {
        return userDetailsRepository.findByPrimaryEmail(primaryEmail)
                .orElseThrow(() -> {
                    log.info("User Details not found for primaryEmail {}", primaryEmail);
                    return new NotFoundException("User Details not found for primaryEmail " + primaryEmail);
                });
    }

    @Override
    public UserDto getUserByPrimaryEmail(String primaryEmail) {
        User user = findUserDetailsByPrimaryEmail(primaryEmail).getUser();
        return userMapper.userToDto(user);
    }

    @Override
    public UserSoiDto getUserSoi(UUID systemUserId) {
        User user = findUser(systemUserId);
        return userMapper.userToUserSoi(user);
    }

    @Override
    public Page<UserDto> search(String name, UserSearchSort sortBy, Sort.Direction sortDirection,
                                int page, int size) {
        Sort sort = sortBy.getSort(sortDirection);
        PageRequest sortedPage = PageRequest.of(page, size, sort);

        List<Specification<User>> andSpec = new ArrayList<>();
        andSpec.add(searchByName(name));

        Specification<User> spec = Specification.allOf(andSpec);
        Page<User> users = userRepository.findAll(spec, sortedPage);

        return users.map(userMapper::userToDto);
    }

    Specification<User> searchByName(String fullName) {
        if (isBlank(fullName)) {
            return null;
        }

        String[] names = fullName.split(KEYWORD_DELIMITER);
        List<Specification<User>> orSpec = new ArrayList<>();
        for (String name : names) {
            orSpec.add(hasUserDetailField("firstName", name));
            orSpec.add(hasUserDetailField("middleName", name));
            orSpec.add(hasUserDetailField("lastName", name));
        }
        return Specification.anyOf(orSpec);
    }

    /**
     * JPA specification to search by userDetail field.
     *
     * @param field the userDetail field to search.
     * @param param the param to search
     * @return A specification for the userDetail criteria
     */
    Specification<User> hasUserDetailField(String field, String param) {
        if (isBlank(param)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
          Path<Set<User>> userDetails = root.get("userDetails");
          return criteriaBuilder.like(criteriaBuilder.lower(userDetails.get(field)),
                  criteriaBuilder.lower(criteriaBuilder.literal("%" + param + "%")));
        };
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID systemUserId, UpdateUserRequest request) {
        User user = findUser(systemUserId);
        UserDetails currentDetails = user.getCurrentUserDetails();

        if (currentDetails != null) {
            currentDetails.setKnownToDate(OffsetDateTime.now());
        }

        UserDetails newDetails = userMapper.updateRequestToUserDetails(request);
        newDetails.setUser(user);
        user.getUserDetails().add(newDetails);

        user = userRepository.save(user);
        log.info("Updated user details for systemUserId {}", systemUserId);
        return userMapper.userToDto(user);
    }

    @Override
    @Transactional
    public void recordLogin(UUID systemUserId) {
        User user = findUser(systemUserId);
        user.setLastLoginDate(Instant.now());
        userRepository.save(user);
        log.info("Recorded login for user {}", systemUserId);
    }

    @Override
    @Transactional
    public void lockUser(UUID systemUserId, int failedAttempts) {
        User user = findUser(systemUserId);
        user.setLocked(YesNo.YES);
        user.setFailedLoginAttempts(failedAttempts);
        userRepository.save(user);
        log.info("Locked user {}", systemUserId);
        try {
            acmClient.lockUser(systemUserId.toString());
        } catch (Exception e) {
            log.warn("Failed to sync lock state to ACM for user {} — may be running in scheduler context: {}", systemUserId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void unlockUser(UUID systemUserId) {
        User user = findUser(systemUserId);
        user.setLocked(YesNo.NO);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        log.info("Unlocked user {}", systemUserId);
        try {
            String email = user.getCurrentUserDetails().getPrimaryEmail();
            keycloakManagerClient.unlockInKeycloak(email);
        } catch (Exception e) {
            log.warn("Failed to unlock user {} in Keycloak: {}", systemUserId, e.getMessage());
        }
        try {
            acmClient.unlockUser(systemUserId.toString());
        } catch (Exception e) {
            log.warn("Failed to sync unlock state to ACM for user {}: {}", systemUserId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deactivateUser(UUID systemUserId) {
        User user = findUser(systemUserId);
        user.setActive(YesNo.NO);

        UserDetails currentDetails = user.getCurrentUserDetails();
        if (currentDetails != null) {
            currentDetails.setKnownToDate(OffsetDateTime.now());
        }

        userRepository.save(user);
        log.info("Deactivated user {}", systemUserId);
    }
}
