package in.batur.eksiclone.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class JwtHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(JwtHealthIndicator.class);
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
        
        try {
            boolean isHealthy = healthCheckConfig.isJwtServiceHealthy();
            
            if (isHealthy) {
                return Health.up()
                        .withDetail("message", "JWT service is up")
                        .build();
            } else {
                logger.warn("JWT service health check failed");
                return Health.down()
                        .withDetail("message", "JWT service is down or unreachable")
                        .build();
            }
        } catch (Exception e) {
            logger.error("Exception during JWT service health check", e);
            return Health.down()
                    .withDetail("message", "JWT service health check error: " + e.getMessage())
                    .withException(e)
                    .build();
        }
    }
}