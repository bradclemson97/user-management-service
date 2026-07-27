package com.example.usermanagementservice.scheduler;

import com.example.usermanagementservice.client.KeycloakManagerClient;
import com.example.usermanagementservice.client.response.KeycloakLockoutStatusResponse;
import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.enums.YesNo;
import com.example.usermanagementservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLockSyncScheduler {

    private static final int LOCK_THRESHOLD = 5;

    private final UserRepository userRepository;
    private final KeycloakManagerClient keycloakManagerClient;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void syncLockedUsers() {
        List<User> activeUsers = userRepository.findByActive(YesNo.YES);
        int locked = 0;
        for (User user : activeUsers) {
            String email = user.getCurrentUserDetails() != null
                    ? user.getCurrentUserDetails().getPrimaryEmail()
                    : null;
            if (email == null) continue;
            try {
                KeycloakLockoutStatusResponse status = keycloakManagerClient.getLockoutStatus(email);
                if (status.isLockedByKeycloak() || status.getFailedAttempts() >= LOCK_THRESHOLD) {
                    if (!YesNo.YES.equals(user.getLocked())) {
                        user.setLocked(YesNo.YES);
                        user.setFailedLoginAttempts(status.getFailedAttempts());
                        userRepository.save(user);
                        locked++;
                        log.info("Locked user {} after {} failed Keycloak attempts",
                                user.getSystemUserId(), status.getFailedAttempts());
                    }
                }
            } catch (Exception e) {
                log.debug("Could not retrieve lockout status for user {}: {}",
                        user.getSystemUserId(), e.getMessage());
            }
        }
        if (locked > 0) {
            log.info("Lock sync complete — locked {} user(s)", locked);
        }
    }
}
