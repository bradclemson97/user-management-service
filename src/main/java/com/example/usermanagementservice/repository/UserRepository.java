package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
