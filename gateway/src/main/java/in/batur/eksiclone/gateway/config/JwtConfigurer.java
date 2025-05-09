package in.batur.eksiclone.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

@Configuration
public class JwtConfigurer {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    @Primary
    ReactiveJwtDecoder reactiveJwtDecoder() {
        try {
            // Doğrudan JWKS endpoint'ini kullanarak JWT decoder oluşturuyoruz
            // OpenID Connect discovery sürecini atlıyoruz
            return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } catch (Exception e) {
            System.err.println("Error creating JWT decoder: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not create JWT decoder", e);
        }
    }
}