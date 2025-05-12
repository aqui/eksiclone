package in.batur.eksiclone.topicservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TopicServiceSecurityConfig {
    
    @Bean
    @Order(1)
    SecurityFilterChain publicEndpointsFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.anyRequest().permitAll()
            )
            .build();
    }
    
    @Bean
    @Order(2)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/v1/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                // Tüm istekler için yetkilendirme, token kontrolü Gateway üzerinden gerçekleşecek
                auth.anyRequest().permitAll()
            )
            .build();
    }
}