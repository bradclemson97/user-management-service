package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.domain.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    /**
     * Returns the User Details entity associated to an email address if one exists.
     *
     * @param primaryEmail the primary email of the user.
     * @return the optional instance of the User Details Entity.
     */
    Optional<UserDetails> findByPrimaryEmail(String primaryEmail);

}

