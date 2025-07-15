package com.example.usermanagementservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Parent class of all Entities in Domain.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Valid
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class JpaAuditEntity {

    /**
     * The user who created the entity.
     */
    @Column(nullable = false)
    @CreatedBy
    private UUID createdBy;

    /**
     * When the entity was created.
     */
    @CreatedDate
    @Column(nullable = false)
    private OffsetDateTime createdDate;

    /**
     * The user who modified the entity.
     */
    @LastModifiedBy
    @Column(nullable = false)
    private UUID modifiedBy;

    /**
     * When the entity was modified.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime modifiedDate;
}
