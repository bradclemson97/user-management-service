package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.domain.JpaAuditEntity;
import com.example.usermanagementservice.model.UserAuditSoi;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Oracle Corporation)"
)
@Component
public class JpaAuditMapperImpl implements JpaAuditMapper {

    @Override
    public UserAuditSoi createdToAuditSoi(JpaAuditEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UserAuditSoi.UserAuditSoiBuilder userAuditSoi = UserAuditSoi.builder();

        userAuditSoi.user( uuidToUserSoiDto( entity.getCreatedBy() ) );
        userAuditSoi.dateTime( instantToOffsetDateTime( entity.getCreatedDate() ) );

        return userAuditSoi.build();
    }

    @Override
    public UserAuditSoi modifiedToAuditSoi(JpaAuditEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UserAuditSoi.UserAuditSoiBuilder userAuditSoi = UserAuditSoi.builder();

        userAuditSoi.user( uuidToUserSoiDto( entity.getModifiedBy() ) );
        userAuditSoi.dateTime( instantToOffsetDateTime( entity.getModifiedDate() ) );

        return userAuditSoi.build();
    }
}
