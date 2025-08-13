package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.response.CreateUserResponse;
import com.example.usermanagementservice.model.UserDetailsDto;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import com.example.usermanagementservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private CreateUserRequest createUserRequest;
    private CreateUserResponse createUserResponse;
    private UserDto userDto;
    private UserSoiDto userSoiDto;
    private UserDetailsDto userDetailsDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        createUserRequest = CreateUserRequest.builder()
                .systemUserId(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        createUserResponse = CreateUserResponse.builder()
                .systemUserId(userId)
                .password("dummy-password")
                .build();

        userDetailsDto = UserDetailsDto.builder()
                .primaryEmail("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        userDto = UserDto.builder()
                .systemUserId(userId)
                .userDetails(userDetailsDto)
                .build();

        userSoiDto = UserSoiDto.builder()
                .systemUserId(userId)
                .primaryEmail("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void createUser_success() throws Exception {
        Mockito.when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createUserResponse);

        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.systemUserId").value(userId.toString()))
                .andExpect(jsonPath("$.password").value("dummy-password"));
    }

    @Test
    void getUser_success() throws Exception {
        Mockito.when(userService.getUser(eq(userId))).thenReturn(userDto);

        mockMvc.perform(get("/v1/user/{systemUserId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemUserId").value(userId.toString()));
    }

    @Test
    void getUserByPrimaryEmail_success() throws Exception {
        Mockito.when(userService.getUserByPrimaryEmail("john.doe@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/v1/user/email/{primaryEmailId}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemUserId").value(userId.toString()));
    }

    @Test
    void getCurrentUser_success() throws Exception {
        Mockito.when(userService.getUser(eq(userId))).thenReturn(userDto);

        mockMvc.perform(get("/v1/user/current")
                        .principal(() -> "dummyPrincipal")
                        .requestAttr("systemUserId", userId)) // Simulate AuthenticationPrincipal
                .andExpect(status().isOk());
    }

    @Test
    void getUserSoi_success() throws Exception {
        Mockito.when(userService.getUserSoi(eq(userId))).thenReturn(userSoiDto);

        mockMvc.perform(get("/v1/user/soi/{systemUserId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemUserId").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void userSearch_success() throws Exception {
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto), PageRequest.of(0, 25), 1);
        Mockito.when(userService.search(anyString(), any(), any(), anyInt(), anyInt())).thenReturn(userPage);

        mockMvc.perform(get("/v1/user")
                        .param("name", "John Smith")
                        .param("sortBy", "NAME")
                        .param("sortDirection", "ASC")
                        .param("pageNumber", "0")
                        .param("pageSize", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].systemUserId").value(userId.toString()));
    }
}

