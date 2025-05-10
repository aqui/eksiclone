package in.batur.eksiclone.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final ReactiveJwtDecoder jwtDecoder;

    public SecurityConfig(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> claims = jwt.getClaims();
            
            if (claims.containsKey("roles")) {
                try {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get("roles");
                    
                    if (roles == null || roles.isEmpty()) {
                        log.warn("JWT içerisinde roller listesi boş: {}", jwt.getSubject());
                        return Flux.empty();
                    }
                    
                    return Flux.fromIterable(roles)
                            .filter(role -> role != null && !role.isBlank())
                            .map(role -> {
                                // Spring Security "hasRole" otomatik olarak "ROLE_" öneki ekler,
                                // JWT'deki roller zaten "ROLE_" ile başlıyorsa olduğu gibi kullan
                                if (role.startsWith("ROLE_")) {
                                    return new org.springframework.security.core.authority.SimpleGrantedAuthority(role);
                                } else {
                                    return new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role);
                                }
                            });
                    
                } catch (ClassCastException e) {
                    log.error("JWT'den rolleri ayrıştırırken hata: {}", e.getMessage());
                    return Flux.empty();
                }
            } else {
                log.warn("JWT içinde 'roles' alanı bulunamadı: {}", jwt.getSubject());
                return Flux.empty();
            }
        });
        
        return jwtAuthenticationConverter;
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            // Disable CSRF
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            
            // CORS configuration
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            
            // Endpoint access rules
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/auth/jwks.json", "/api/v1/auth/health").permitAll()
                .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .pathMatchers("/api/v1/public/**").permitAll()
                
                // Protected endpoints
                .pathMatchers("/api/v1/users/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers("/api/v1/roles/**").hasRole("ADMIN")
                .pathMatchers("/api/v1/topics/**").authenticated()
                .pathMatchers("/api/v1/entries/**").authenticated()
                
                // All others require authentication
                .anyExchange().authenticated()
            )
            
            // JWT configuration
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtDecoder(jwtDecoder)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            
            // Error handling  
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((exchange, exception) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    String errorMessage = "Authentication required: " + exception.getMessage();
                    
                    String responseBody = "{\"error\":\"unauthorized\",\"message\":\"" + errorMessage + "\"}";
                    return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes()))
                    );
                })
                .accessDeniedHandler((exchange, exception) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    String responseBody = "{\"error\":\"forbidden\",\"message\":\"You don't have permission to access this resource\"}";
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