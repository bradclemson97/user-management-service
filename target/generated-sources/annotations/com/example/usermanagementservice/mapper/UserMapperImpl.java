package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.client.request.KeycloakCreateUserRequest;
import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.controller.request.UpdateUserRequest;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Override
    public User requestToUser(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder<?, ?> user = User.builder();

        user.userDetails( toSet( requestToUserDetails( request ) ) );
        user.systemUserId( request.getSystemUserId() );

        return user.build();
    }

    @Override
    public KeycloakCreateUserRequest requestToKeycloak(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        KeycloakCreateUserRequest.KeycloakCreateUserRequestBuilder keycloakCreateUserRequest = KeycloakCreateUserRequest.builder();

        keycloakCreateUserRequest.systemUserId( request.getSystemUserId() );
        keycloakCreateUserRequest.firstName( request.getFirstName() );
        keycloakCreateUserRequest.middleName( request.getMiddleName() );
        keycloakCreateUserRequest.lastName( request.getLastName() );
        keycloakCreateUserRequest.email( request.getEmail() );

        return keycloakCreateUserRequest.build();
    }

    @Override
    public UserDetails requestToUserDetails(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        UserDetails.UserDetailsBuilder<?, ?> userDetails = UserDetails.builder();

        userDetails.primaryEmail( request.getEmail() );
        userDetails.firstName( request.getFirstName() );
        userDetails.middleName( request.getMiddleName() );
        userDetails.lastName( request.getLastName() );

        return userDetails.build();
    }

    @Override
    public UserDetails updateRequestToUserDetails(UpdateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        UserDetails.UserDetailsBuilder<?, ?> userDetails = UserDetails.builder();

        userDetails.title( request.getTitle() );
        userDetails.firstName( request.getFirstName() );
        userDetails.middleName( request.getMiddleName() );
        userDetails.lastName( request.getLastName() );
        userDetails.primaryEmail( request.getPrimaryEmail() );

        return userDetails.build();
    }

    @Override
    public UserDto userToDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.userDetails( userDetailsMapper.userDetailsToDto( user.getCurrentUserDetails() ) );
        userDto.systemUserId( user.getSystemUserId() );
        userDto.active( user.getActive() );

        return userDto.build();
    }

    @Override
    public UserSoiDto userToUserSoi(User user) {
        if ( user == null ) {
            return null;
        }

        UserSoiDto.UserSoiDtoBuilder userSoiDto = UserSoiDto.builder();

        userSoiDto.firstName( userCurrentUserDetailsFirstName( user ) );
        userSoiDto.middleName( userCurrentUserDetailsMiddleName( user ) );
        userSoiDto.lastName( userCurrentUserDetailsLastName( user ) );
        userSoiDto.primaryEmail( userCurrentUserDetailsPrimaryEmail( user ) );
        userSoiDto.systemUserId( user.getSystemUserId() );

        return userSoiDto.build();
    }

    private String userCurrentUserDetailsFirstName(User user) {
        if ( user == null ) {
            return null;
        }
        UserDetails currentUserDetails = user.getCurrentUserDetails();
        if ( currentUserDetails == null ) {
            return null;
        }
        String firstName = currentUserDetails.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String userCurrentUserDetailsMiddleName(User user) {
        if ( user == null ) {
            return null;
        }
        UserDetails currentUserDetails = user.getCurrentUserDetails();
        if ( currentUserDetails == null ) {
            return null;
        }
        String middleName = currentUserDetails.getMiddleName();
        if ( middleName == null ) {
            return null;
        }
        return middleName;
    }

    private String userCurrentUserDetailsLastName(User user) {
        if ( user == null ) {
            return null;
        }
        UserDetails currentUserDetails = user.getCurrentUserDetails();
        if ( currentUserDetails == null ) {
            return null;
        }
        String lastName = currentUserDetails.getLastName();
        if ( lastName == null ) {
            return null;
        }
        return lastName;
    }

    private String userCurrentUserDetailsPrimaryEmail(User user) {
        if ( user == null ) {
            return null;
        }
        UserDetails currentUserDetails = user.getCurrentUserDetails();
        if ( currentUserDetails == null ) {
            return null;
        }
        String primaryEmail = currentUserDetails.getPrimaryEmail();
        if ( primaryEmail == null ) {
            return null;
        }
        return primaryEmail;
    }
}
