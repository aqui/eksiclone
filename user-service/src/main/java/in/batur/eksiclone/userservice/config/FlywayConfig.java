package in.batur.eksiclone.userservice.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FlywayConfig {

    @Value("${default.admin.password:password123}")
    private String defaultAdminPassword;

    @Value("${default.moderator.password:password123}")
    private String defaultModeratorPassword;

    @Value("${default.user.password:password123}")
    private String defaultUserPassword;

    @Bean
    Flyway flyway(DataSource dataSource) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // Hash passwords
        String hashedAdminPassword = passwordEncoder.encode(defaultAdminPassword);
        String hashedModeratorPassword = passwordEncoder.encode(defaultModeratorPassword);
        String hashedUserPassword = passwordEncoder.encode(defaultUserPassword);
        
        // Create placeholders
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("DEFAULT_ADMIN_PASSWORD", hashedAdminPassword);
        placeholders.put("DEFAULT_MODERATOR_PASSWORD", hashedModeratorPassword);
        placeholders.put("DEFAULT_USER_PASSWORD", hashedUserPassword);
        
        // Configure Flyway
        FluentConfiguration configuration = Flyway.configure()
                .dataSource(dataSource)
                .placeholders(placeholders);
        
        // Create and start Flyway
        Flyway flyway = new Flyway(configuration);
        flyway.migrate();
        
        return flyway;
    }
}