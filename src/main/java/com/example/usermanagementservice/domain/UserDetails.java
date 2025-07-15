package com.example.usermanagementservice.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.dialect.H2Dialect;

import java.time.OffsetDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

/**
 * a user can have a number of user detail records but only one will be active at any time.
 * This structure supports the requirements to remove someone's access to the system because they
 * no longer need access, but to allow them to rejoin at a later date without having to issue a new username.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@Valid
@Entity
@Table(name = "user_details")
public class UserDetails extends JpaAuditEntity implements Comparable<UserDetails> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "udl_id")
    private Long id;

    @Generated
    @ColumnDefault("gen_random_uuid()")
    @DialectOverride.ColumnDefault(dialect = H2Dialect.class,
            override = @ColumnDefault("gen_random_uuid()"))
    @Column(nullable = false, insertable = false, updatable = false, unique = true)
    private UUID userDetailId;

    @Column
    private String title;

    @Column
    private String firstName;

    @Column
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String primaryEmail;

    @Column(nullable = false)
    @Builder.Default
    private OffsetDateTime knownFromDate = OffsetDateTime.now();

    @Column
    private OffsetDateTime knownToDate;

    @ManyToOne
    @JoinColumn(name = "usr_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private User user;

    /**
     * Compare two UserDetails to determine order.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
     * or greater than the specified object.
     */
    @Override
    public int compareTo(UserDetails o) {
        return o.getKnownFromDate().compareTo(getKnownFromDate());
    }

}
