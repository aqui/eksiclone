package in.batur.eksiclone.userservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import in.batur.eksiclone.security.config.KeyStoreProperties;
import in.batur.eksiclone.security.config.JwtProperties;

@Configuration
@EnableConfigurationProperties
public class SecurityPropertiesConfig {
    
    @Bean
    KeyStoreProperties keyStoreProperties() {
        return new KeyStoreProperties();
    }
    
    @Bean
    JwtProperties jwtProperties() {
        return new JwtProperties();
    }
}