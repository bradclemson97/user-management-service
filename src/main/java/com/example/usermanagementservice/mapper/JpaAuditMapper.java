package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.model.UserSoiDto;
import org.mapstruct.Mapper;
import com.example.usermanagementservice.domain.JpaAuditEntity;
import com.example.usermanagementservice.model.UserAuditSoi;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Mapper for {@link JpaAuditEntity}
 * and {@link UserAuditSoi }
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface JpaAuditMapper {

    @Mapping(source = "entity.createdBy", target = "user", qualifiedByName = "uuidToUserSoiDto")
    @Mapping(source = "entity.createdDate", target = "dateTime")
    @Named("createdToAuditSoi")
    UserAuditSoi createdToAuditSoi(JpaAuditEntity entity);

    @Mapping(source = "entity.modifiedBy", target = "user", qualifiedByName = "uuidToUserSoiDto")
    @Mapping(source = "entity.modifiedDate", target = "dateTime")
    @Named("modifiedToAuditSoi")
    UserAuditSoi modifiedToAuditSoi(JpaAuditEntity entity);

    // Conversion from UUID to UserSoiDto
    @Named("uuidToUserSoiDto")
    default UserSoiDto uuidToUserSoiDto(UUID value) {
        if (value == null) {
            return null;  // return null if UUID is null
        }
        return UserSoiDto.builder()  // Assuming UserSoiDto has a builder pattern
                .systemUserId(value)  // Set UUID or use it as needed
                .build();
    }

}
