package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.domain.UserDetails;
import com.example.usermanagementservice.model.UserDetailsDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Oracle Corporation)"
)
@Component
public class UserDetailsMapperImpl implements UserDetailsMapper {

    @Override
    public UserDetailsDto userDetailsToDto(UserDetails details) {
        if ( details == null ) {
            return null;
        }

        UserDetailsDto.UserDetailsDtoBuilder userDetailsDto = UserDetailsDto.builder();

        userDetailsDto.userDetailId( details.getUserDetailId() );
        userDetailsDto.title( details.getTitle() );
        userDetailsDto.firstName( details.getFirstName() );
        userDetailsDto.middleName( details.getMiddleName() );
        userDetailsDto.lastName( details.getLastName() );
        userDetailsDto.primaryEmail( details.getPrimaryEmail() );
        userDetailsDto.knownFromDate( details.getKnownFromDate() );
        userDetailsDto.knownToDate( details.getKnownToDate() );

        return userDetailsDto.build();
    }
}
