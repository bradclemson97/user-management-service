package com.example.usermanagementservice.service;

import com.example.usermanagementservice.client.AcmClient;
import com.example.usermanagementservice.client.KeycloakManagerClient;
import com.example.usermanagementservice.client.request.AcmCreateUserRequest;
import com.example.usermanagementservice.client.response.KeycloakCreateUserResponse;
import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.domain.enums.UserSearchSort;
import com.example.usermanagementservice.exception.ConflictException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.repository.UserDetailsRepository;
import com.example.usermanagementservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
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

        assertThrows(EntityNotFoundException.class,
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
}

