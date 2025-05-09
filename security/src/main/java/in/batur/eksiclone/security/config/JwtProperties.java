package in.batur.eksiclone.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")  // "spring.jwt" yerine "jwt" kullanıyoruz
public class JwtProperties {
    private long expiration = 86400000; // 24 saat (milisaniye)
    private long refreshExpiration = 604800000; // 7 gün (milisaniye)

    // Getter ve Setter metotları
    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}