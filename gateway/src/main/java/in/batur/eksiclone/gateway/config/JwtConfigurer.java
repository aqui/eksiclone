package in.batur.eksiclone.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

@Configuration
public class JwtConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtConfigurer.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:8081/api/v1/auth/jwks.json}")
    private String jwkSetUri;

    @Bean
    @Primary
    ReactiveJwtDecoder reactiveJwtDecoder() {
        try {
            logger.info("Creating JWT decoder with JWK Set URI: {}", jwkSetUri);
            // Doğrudan JWKS endpoint'ini kullanarak JWT decoder oluşturuyoruz
            // OpenID Connect discovery sürecini atlıyoruz
            return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } catch (Exception e) {
            logger.error("Error creating JWT decoder: {}", e.getMessage(), e);
            throw new RuntimeException("Could not create JWT decoder", e);
        }
    }
}