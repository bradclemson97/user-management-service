package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.enums.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Returns the User entity associated to a unique system user id if one exists.
     *
     * @param systemUserId the unique User ID of the user.
     * @return the optional instance of the User Entity
     */
    Optional<User> findBySystemUserId(UUID systemUserId);

    /**
     * Returns if a user with a system user id exists/
     *
     * @param systemUserId the unique system user id of a user.
     * @return if the system user id exists.
     */
    boolean existsBySystemUserId(UUID systemUserId);

    /**
     * Finds active users whose last activity (login date, or account creation date if never logged in)
     * is before the given threshold — used by the inactivity deactivation scheduler.
     */
    @Query("SELECT u FROM User u WHERE u.active = :active AND COALESCE(u.lastLoginDate, u.createdDate) < :threshold")
    List<User> findActiveUsersWithLastActivityBefore(@Param("active") YesNo active, @Param("threshold") Instant threshold);

    /**
     * Finds all users by their active status — used by the lock sync scheduler.
     */
    List<User> findByActive(YesNo active);
}
