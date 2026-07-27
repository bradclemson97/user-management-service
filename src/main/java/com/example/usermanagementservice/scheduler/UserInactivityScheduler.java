package com.example.usermanagementservice.scheduler;

import com.example.usermanagementservice.domain.User;
import com.example.usermanagementservice.domain.enums.YesNo;
import com.example.usermanagementservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInactivityScheduler {

    private final UserRepository userRepository;

    private static final int INACTIVITY_THRESHOLD_DAYS = 60;

    /**
     * Runs daily at midnight. Deactivates any active user whose last login date
     * (or account creation date, if they have never logged in) is more than 60 days ago.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateInactiveUsers() {
        Instant threshold = Instant.now().minus(INACTIVITY_THRESHOLD_DAYS, ChronoUnit.DAYS);
        List<User> usersToDeactivate = userRepository.findActiveUsersWithLastActivityBefore(YesNo.YES, threshold);

        if (usersToDeactivate.isEmpty()) {
            log.info("Inactivity check: no users to deactivate");
            return;
        }

        usersToDeactivate.forEach(user -> user.setActive(YesNo.NO));
        userRepository.saveAll(usersToDeactivate);
        log.info("Inactivity check: deactivated {} user(s) with no activity in the last {} days",
                usersToDeactivate.size(), INACTIVITY_THRESHOLD_DAYS);
    }
}
