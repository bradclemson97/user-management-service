package com.example.usermanagementservice.client;

import com.example.usermanagementservice.client.config.FeignClientConfig;
import com.example.usermanagementservice.client.request.KeycloakCreateUserRequest;
import com.example.usermanagementservice.client.response.KeycloakCreateUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}
