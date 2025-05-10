package in.batur.eksiclone.userservice.config;

import in.batur.eksiclone.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class UserServiceSecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // Constructor injection
    public UserServiceSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary // Birincil bean olduğunu belirtiyoruz
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    @Order(1)
    SecurityFilterChain publicEndpointsFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/v1/auth/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
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
                auth.requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                   .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")
                   .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasRole("ADMIN")
                   .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                   .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ADMIN", "USER", "MODERATOR")
                   .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Authentication required\"}");
                })
            )
            .build();
    }
}