package in.batur.eksiclone.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    // Changed to optional autowiring to allow the application to start even if auth-service is down
    @Autowired(required = false)
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // JWT ve OAuth2 ile güvenli bir gateway yapılandırması
        return http
            // CSRF korumasını devre dışı bırak (token tabanlı güvenlik için gerekli değil)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            
            // CORS yapılandırması
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            
            // OAuth 2.0 / OpenID Connect desteği - Conditionally apply if clientRegistrationRepository exists
            .oauth2Login(oauth2LoginSpec -> {
                if (clientRegistrationRepository != null) {
                    oauth2LoginSpec.authenticationSuccessHandler(
                        (webFilterExchange, authentication) -> webFilterExchange.getExchange().getResponse().setComplete()
                    );
                }
            })
            
            // OAuth 2.0 / JWT resource server yapılandırması  
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
            
            // OAuth 2.0 / OpenID Connect çıkış işleyicisi - Conditionally apply if clientRegistrationRepository exists
            .logout(logoutSpec -> {
                if (clientRegistrationRepository != null) {
                    logoutSpec.logoutSuccessHandler(logoutSuccessHandler());
                }
            })
            
            // Yetkilendirme kuralları
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers("/api/v1/auth/**", "/actuator/**", 
                              "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Kimlik doğrulama gerektiren API'ler
                .pathMatchers("/api/v1/users/**").authenticated()
                .pathMatchers("/api/v1/roles/**").hasAuthority("ROLE_ADMIN")
                // Diğer tüm istekler için kimlik doğrulama gerekiyor
                .anyExchange().authenticated()
            )
            
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", 
                                                     "Accept", "Origin", "Cache-Control"));
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count", "Link", 
                                                     "Access-Control-Allow-Origin", 
                                                     "Access-Control-Allow-Credentials"));
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    ServerLogoutSuccessHandler logoutSuccessHandler() {
        if (clientRegistrationRepository != null) {
            OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                    new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
            oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
            return oidcLogoutSuccessHandler;
        }
        // Default handler if OAuth is not available
        return (exchange, authentication) -> exchange.getExchange().getResponse().setComplete();
    }
}