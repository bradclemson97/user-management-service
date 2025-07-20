package com.example.usermanagementservice.client;

import com.example.usermanagementservice.client.config.FeignClientConfig;
import com.example.usermanagementservice.client.model.AcmUserDto;
import com.example.usermanagementservice.client.request.AcmCreateUserRequest;
import com.example.usermanagementservice.client.response.AcmCreateUserResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Client for the Access Control Manager.
 */
@FeignClient(value = "accessControlClient", url = "${ACM_URL:http://localhost:8130}",
configuration = FeignClientConfig.class)
public interface AcmClient {

    /**
     * creates a new User Instance in ACM
     *
     * @return the systemUserId of the new User
     */
    @PostMapping("v1/users")
    AcmCreateUserResponse createUser(@RequestBody AcmCreateUserRequest request);

    /**
     * Get user information for the provided systemUserId. This information is cached temporally on a
     * systemUserId basis to prevent exessive duplicate calls. The cache settings are managed in
     * the application config.
     *
     * @param systemUserId the unique system ID of the user
     * @return the access control information for a user.
     */
    @Cacheable(value = "acm_user_cache")
    @GetMapping("/v1/users/{systemUserId")
    AcmUserDto getUser(@PathVariable String systemUserId);
}
