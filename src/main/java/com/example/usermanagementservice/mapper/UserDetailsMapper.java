package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.model.UserDetailsDto;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "string",
nullValuePropertyMappingStrategy = IGNORE, nullValueIterableMappingStrategy = RETURN_DEFAULT)
public interface UserDetailsMapper {

    /**
     * map a userDetails to a userDetails dto
     *
     * @param details UserDetails
     * @return UserDetailsDto
     */
    UserDetailsDto userDetailsToDto(UserDetails details);

}