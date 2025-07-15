package com.example.usermanagementservice.mapper;

import org.mapstruct.Mapper;
import com.example.usermanagementservice.domain.JpaAuditEntity;
import com.example.usermanagementservice.model.UserAuditSoi;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Mapper for {@link JpaAuditEntity}
 * and {@link UserAuditSoi }
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface JpaAuditMapper {

    /**
     * Method to map a {@link JpaAuditEntity} to the created {@link UserAuditSoi}
     *
     * @param entity the {@link JpaAuditEntity} to map.
     * @return the mapped {@link UserAuditSoi} for created.
     */
    @Mapping(source = "entity.createdBy", target = "user")
    @Mapping(source = "entity.createdDate", target = "dateTime")
    @Named("createdToAuditSoi")
    UserAuditSoi createdToAuditSoi(JpaAuditEntity entity);

    /**
     * Method to map a {@link JpaAuditEntity} to the modified {@link UserAuditSoi}.
     *
     * @param entity the {@link JpaAuditEntity} to map.
     * @return the mapped {@link UserAuditSoi} for modified.
     */
    @Mapping(source = "entity.modifiedBy", target = "user")
    @Mapping(source = "entity.modifiedDate", target = "dateTime")
    @Named("modifiedToAuditSoi")
    UserAuditSoi modifiedToAuditSoi(JpaAuditEntity entity);

}
