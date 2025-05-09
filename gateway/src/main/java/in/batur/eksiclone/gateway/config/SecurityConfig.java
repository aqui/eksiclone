package in.batur.eksiclone.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveJwtDecoder jwtDecoder;

    public SecurityConfig(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            // Disable CSRF
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            
            // CORS configuration
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            
            // Endpoint access rules - Tüm istekleri geçici olarak permitAll yapabiliriz
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/auth/jwks.json", "/api/v1/auth/health").permitAll()
                .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .pathMatchers("/api/v1/public/**").permitAll()
                // Geçici olarak tüm isteklere izin veriyoruz
                .anyExchange().permitAll()
            )
            
            // JWT configuration
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder))
            )
            
            // Error handling  
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((exchange, exception) -> {
                    System.err.println("Authentication error: " + exception.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    String errorMessage = "Kimlik doğrulama gerekli: " + exception.getMessage();
                    
                    String responseBody = "{\"error\":\"unauthorized\",\"message\":\"" + errorMessage + "\"}";
                    return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes()))
                    );
                })
                .accessDeniedHandler((exchange, exception) -> {
                    System.err.println("Access denied error: " + exception.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    String responseBody = "{\"error\":\"forbidden\",\"message\":\"Bu kaynağa erişim yetkiniz yok\"}";
                    return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes()))
                    );
                })
            )
            
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count", "Link"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean("securityWebClient")
    WebClient webClient() {
        return WebClient.builder().build();
    }
}