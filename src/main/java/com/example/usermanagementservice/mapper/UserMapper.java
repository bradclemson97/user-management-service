package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.controller.request.CreateUserRequest;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.model.UserDto;
import com.example.usermanagementservice.model.UserSoiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = IGNORE, nullValueIterableMappingStrategy = RETURN_DEFAULT,
uses = {UserDetailsMapper.class})
public interface UserMapper {

    /**
     * map a request to an entity.
     *
     * @param request UserRequest
     * @return User
     */
    @Mapping(source = "request", target = "userDetails")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    User requestToUser(CreateUserRequest request);

    /**
     * map a request to an entity.
     *
     * @param request UserRequest
     * @return UserDetails
     */
    @Mapping(source = "email", target = "primaryEmail")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userDetailId", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "knownFromDate", ignore = true)
    @Mapping(target = "knownToDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    UserDetails requestToUserDetails(CreateUserRequest request);

    /**
     * map a user to a user dto
     *
     * @param user User
     * @return UserDto
     */
    @Mapping(source = "currentUserDetails", target = "userDetails")
    UserDto userToDto(User user);

    /**
     * map a user to a user SOI dto.
     *
     * @param user User.
     * @return user SOI dto.
     */
    @Mapping(source = "currentUserDetails.firstName", target = "firstName")
    @Mapping(source = "currentUserDetails.middleName", target = "middleName")
    @Mapping(source = "currentUserDetails.lastName", target = "lastName")
    @Mapping(source = "currentUserDetails.primaryEmail", target = "primaryEmail")
    UserSoiDto userToUserSoi(User user);

    /**
     * Generic method to convert a singular object into a set for map struct.
     *
     * @param singular object.
     * @param <T> the type of the singular object.
     * @return set of containing the singular object.
     */
    default <T> Set<T> toSet(T singular) {
        if (isNull(singular)) {
            return new HashSet<>();
        }
        return new HashSet<>(Set.of(singular));
    }

}
