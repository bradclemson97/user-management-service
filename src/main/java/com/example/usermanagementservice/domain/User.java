package com.example.usermanagementservice.domain;


import com.example.usermanagementservice.domain.enums.YesNo;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.usermanagementservice.domain.enums.YesNo.NO;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

/**
 * A named user account that is permitted to access the system.
 * This is generally a person but there will be accounts set up for automatic processes.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@Valid
@Entity
@Table(name = "users")
public class User extends JpaAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Long id;

    @Column(nullable = false)
    private UUID systemUserId;

    @Column(name = "locked_user_ind", nullable = false)
    @Enumerated(STRING)
    @Builder.Default
    private YesNo locked = NO;

    @OneToMany(mappedBy = "user", cascade = ALL, fetch = EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<UserDetails> userDetails = new HashSet<>();

    /**
     * A callback method for the INSERT lifecycle event.
     */
    @PrePersist
    public void prePersist(){cascadeRelationships();}

    private void cascadeRelationships() {
        userDetails.forEach(details -> details.setUser(this));
    }

    /**
     * Get current user details.
     *
     * @return the current user's details.
     */
    public UserDetails getCurrentUserDetails() {
        return userDetails.stream()
                .sorted()
                .findFirst()
                .orElse(null);
    }

}
