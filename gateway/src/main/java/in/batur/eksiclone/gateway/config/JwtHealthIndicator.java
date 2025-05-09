package in.batur.eksiclone.gateway.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

@Component
public class JwtHealthIndicator implements HealthIndicator {

    private final HealthCheckConfig healthCheckConfig;
    private final JwtHealthProperties jwtHealthProperties;
    
    public JwtHealthIndicator(HealthCheckConfig healthCheckConfig, JwtHealthProperties jwtHealthProperties) {
        this.healthCheckConfig = healthCheckConfig;
        this.jwtHealthProperties = jwtHealthProperties;
    }
    
    @Override
    public Health health() {
        // Sadece etkinleştirilmişse kontrolü gerçekleştir
        if (!jwtHealthProperties.isEnabled()) {
            return Health.up()
                    .withDetail("message", "JWT health check disabled")
                    .build();
        }
        
        boolean isHealthy = healthCheckConfig.isJwtServiceHealthy();
        
        if (isHealthy) {
            return Health.up()
                    .withDetail("message", "JWT service is up")
                    .build();
        } else {
            return Health.down()
                    .withDetail("message", "JWT service is down or unreachable")
                    .build();
        }
    }
}