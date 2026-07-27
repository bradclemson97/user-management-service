package com.example.usermanagementservice.client;

import com.example.usermanagementservice.client.config.FeignClientConfig;
import com.example.usermanagementservice.client.request.KeycloakCreateUserRequest;
import com.example.usermanagementservice.client.response.KeycloakCreateUserResponse;
import com.example.usermanagementservice.client.response.KeycloakLockoutStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Client for the Keycloak Manager.
 */
@FeignClient(value = "KeycloakManagerClient", url = "${KEYCLOAK_MANAGER_URL:http://localhost:8210}",
configuration = FeignClientConfig.class)
public interface KeycloakManagerClient {

    /**
     * Creates a new User instance in Keycloak.
     *
     * @return the user details and new credentials of the new User.
     */
    @PostMapping("v1/user")
    KeycloakCreateUserResponse createUser(@RequestBody KeycloakCreateUserRequest request);

    /**
     * Rollback a new User creation in Keycloak.
     */
    @DeleteMapping("v1/user/rollback/{primaryEmail}")
    void rollbackCreateUser(@PathVariable String primaryEmail);

    /**
     * Get the lockout status of a user in Keycloak.
     */
    @GetMapping("v1/user/{email}/lockout-status")
    KeycloakLockoutStatusResponse getLockoutStatus(@PathVariable String email);

    /**
     * Unlock a user in Keycloak (clear brute-force state and re-enable).
     */
    @PutMapping("v1/user/{email}/unlock")
    void unlockInKeycloak(@PathVariable String email);
}
