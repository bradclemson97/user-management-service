package com.example.usermanagementservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.model.UserDetailsDto;
import com.example.usermanagementservice.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Mapper Unit Tests")
public class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserDetailsMapper userDetailsMapper;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(mapper, "userDetailsMapper", userDetailsMapper);
    }

    @Test
    @DisplayName("requestToUser - mapps correctly")
    void requestToUser() {
        //Given
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(UUID.randomUUID())
                .firstName("firstName")
                .middleName("middleName")
                .lastName("lastName")
                .email("hello@test.co.uk")
                .build();

        //When
        User result = mapper.requestToUser(request);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getSystemUserId()).isEqualTo(request.getSystemUserId());
        UserDetails userDetails = result.getCurrentUserDetails();
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(userDetails.getMiddleName()).isEqualTo(request.getMiddleName());
        assertThat(userDetails.getLastName()).isEqualTo(request.getLastName());
        assertThat(userDetails.getPrimaryEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("userToDto - mapps correctly")
    void userToDto() {
        //Given
        UserDetails userDetails = UserDetails.builder().build();
        UserDetailsDto userDetailsDto = UserDetailsDto.builder().build();
        given(userDetailsMapper.userDetailsToDto(userDetails)).willReturn(userDetailsDto);

        User user = User.builder()
                .id(1L)
                .systemUserId(UUID.randomUUID())
                .userDetails(Set.of(userDetails))
                .build();

        //When
        UserDto result = mapper.userToDto(user);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getSystemUserId()).isEqualTo(user.getSystemUserId());
        assertThat(result.getUserDetails()).isEqualTo(userDetailsDto);
    }
}
