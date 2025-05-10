package in.batur.eksiclone.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT health check özelliklerini tutan sınıf.
 * Configuration properties ile bean olarak tanımlandı.
 */
@Configuration
@ConfigurationProperties(prefix = "management.health.jwt")
public class JwtHealthProperties {
    private boolean enabled = true;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}