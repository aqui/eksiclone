package in.batur.eksiclone.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManagementHealthConfig {

    @Value("${management.health.jwt.enabled:true}")
    private boolean jwtEnabled;

    @Bean
    JwtHealthProperties jwtHealthProperties() {
        JwtHealthProperties properties = new JwtHealthProperties();
        properties.setEnabled(jwtEnabled);
        return properties;
    }
}