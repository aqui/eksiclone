package in.batur.eksiclone.roleservice.config;

import in.batur.eksiclone.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class RoleServiceSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor injection
    public RoleServiceSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
                auth.requestMatchers(HttpMethod.GET, "/api/v1/roles").hasAnyRole("ADMIN")
                   .requestMatchers(HttpMethod.POST, "/api/v1/roles").hasAnyRole("ADMIN")
                   .requestMatchers(HttpMethod.PUT, "/api/v1/roles/**").hasAnyRole("ADMIN")
                   .requestMatchers(HttpMethod.DELETE, "/api/v1/roles/**").hasAnyRole("ADMIN")
                   .requestMatchers(HttpMethod.GET, "/api/v1/roles/**").hasAnyRole("ADMIN", "USER", "MODERATOR")
                   .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Kimlik doğrulama gerekli\"}");
                })
            )
            .build();
    }
}