package in.batur.eksiclone.userservice.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class HealthIndicator implements org.springframework.boot.actuate.health.HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "user-service")
                .withDetail("status", "running")
                .build();
    }
}