package com.example.usermanagementservice.service;

import com.example.usermanagementservice.client.AcmClient;
import com.example.usermanagementservice.client.KeycloakManagerClient;
import com.example.usermanagementservice.client.request.AcmCreateUserRequest;
import com.example.usermanagementservice.client.response.KeycloakCreateUserResponse;
import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.controller.request.UpdateUserRequest;
import com.example.usermanagementservice.domain.enums.UserSearchSort;
import com.example.usermanagementservice.domain.enums.YesNo;
import com.example.usermanagementservice.exception.ConflictException;
import com.example.usermanagementservice.exception.NotFoundException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.UserAuditRecord;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.repository.UserDetailsRepository;
import com.example.usermanagementservice.repository.UserHistoryRepository;
import com.example.usermanagementservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserDetailsRepository userDetailsRepository;
    @Mock private UserMapper userMapper;
    @Mock private KeycloakManagerClient keycloakManagerClient;
    @Mock private AcmClient acmClient;
    @Mock private UserHistoryRepository userHistoryRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(userId)
                .email("test@example.com")
                .build();

        User user = User.builder().build();
        user.setSystemUserId(userId);

        when(userRepository.existsBySystemUserId(userId)).thenReturn(false);
        when(userMapper.requestToUser(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        KeycloakCreateUserResponse keycloakResponse = KeycloakCreateUserResponse.builder()
                .password("generatedPassword")
                .build();

        when(userMapper.requestToKeycloak(request)).thenReturn(null);
        when(keycloakManagerClient.createUser(any())).thenReturn(keycloakResponse);

        CreateUserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(userId, response.getSystemUserId());
        assertEquals("generatedPassword", response.getPassword());

        verify(acmClient, times(1)).createUser(any(AcmCreateUserRequest.class));
    }

    @Test
    void createUser_conflict_throwsException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(userId)
                .build();

        when(userRepository.existsBySystemUserId(userId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void getUser_success() {
        User user = User.builder().build();
        user.setSystemUserId(userId);
        UserDto userDto = UserDto.builder().build();

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);
        assertEquals(userDto, result);
    }

    @Test
    void getUser_notFound_throwsException() {
        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void getUserByPrimaryEmail_success() {
        String email = "test@example.com";
        UserDetails userDetails = UserDetails.builder().build();
        User user = User.builder().build();
        userDetails.setUser(user);

        UserDto userDto = UserDto.builder().build();

        when(userDetailsRepository.findByPrimaryEmail(email)).thenReturn(Optional.of(userDetails));
        when(userMapper.userToDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserByPrimaryEmail(email);
        assertEquals(userDto, result);
    }

    @Test
    void getUserByPrimaryEmail_notFound_throwsException() {
        when(userDetailsRepository.findByPrimaryEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserByPrimaryEmail("notfound@example.com"));
    }

    @Test
    void search_returnsPagedUsers() {
        String name = "John Doe";
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.ASC;
        UserSearchSort sort = UserSearchSort.NAME;

        User user = User.builder().build();
        UserDto userDto = UserDto.builder().build();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(userPage);
        when(userMapper.userToDto(user)).thenReturn(userDto);

        Page<UserDto> result = userService.search(name, sort, direction, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userDto, result.getContent().get(0));
    }

    @Test
    void getAllUsers_returnsPagedUsers() {
        User user = User.builder().build();
        UserDto userDto = UserDto.builder().build();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        when(userMapper.userToDto(user)).thenReturn(userDto);

        Page<UserDto> result = userService.getAllUsers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userDto, result.getContent().get(0));
    }

    @Test
    void lockUser_success() {
        User user = User.builder().build();
        user.setSystemUserId(userId);

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.lockUser(userId, 5);

        assertEquals(YesNo.YES, user.getLocked());
        assertEquals(5, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
        verify(acmClient).lockUser(userId.toString());
    }

    @Test
    void lockUser_notFound_throwsException() {
        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.lockUser(userId, 3));
    }

    @Test
    void unlockUser_success() {
        UserDetails details = UserDetails.builder().primaryEmail("test@example.com").build();
        User user = User.builder().build();
        user.setSystemUserId(userId);
        user.getUserDetails().add(details);

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.unlockUser(userId);

        assertEquals(YesNo.NO, user.getLocked());
        assertEquals(0, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void unlockUser_notFound_throwsException() {
        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.unlockUser(userId));
    }

    @Test
    void deactivateUser_success() {
        User user = User.builder().build();
        user.setSystemUserId(userId);

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivateUser(userId);

        assertEquals(YesNo.NO, user.getActive());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_notFound_throwsException() {
        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deactivateUser(userId));
    }

    @Test
    void updateUser_success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .primaryEmail("jane.smith@example.com")
                .build();

        User user = User.builder().build();
        user.setSystemUserId(userId);
        UserDetails newDetails = UserDetails.builder()
                .primaryEmail("jane.smith@example.com")
                .build();
        UserDto userDto = UserDto.builder().build();

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.of(user));
        when(userMapper.updateRequestToUserDetails(request)).thenReturn(newDetails);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userId, request);

        assertEquals(userDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_notFound_throwsException() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .primaryEmail("jane.smith@example.com")
                .build();

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void recordLogin_success() {
        User user = User.builder().build();
        user.setSystemUserId(userId);

        when(userRepository.findBySystemUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.recordLogin(userId);

        assertNotNull(user.getLastLoginDate());
        verify(userRepository).save(user);
    }

    @Test
    void getUserHistory_returnsRecordsFromRepository() {
        List<UserAuditRecord> expected = List.of(
                UserAuditRecord.builder()
                        .validFrom(java.time.Instant.now())
                        .lockedUserInd("NO")
                        .activeInd("YES")
                        .failedLoginAttempts(0)
                        .build()
        );

        when(userHistoryRepository.findBySystemUserId(userId)).thenReturn(expected);

        List<UserAuditRecord> result = userService.getUserHistory(userId);

        assertEquals(expected, result);
        verify(userHistoryRepository).findBySystemUserId(userId);
    }

    @Test
    void getUserHistory_returnsEmptyListWhenNoHistory() {
        when(userHistoryRepository.findBySystemUserId(userId)).thenReturn(List.of());

        List<UserAuditRecord> result = userService.getUserHistory(userId);

        assertTrue(result.isEmpty());
    }
}

