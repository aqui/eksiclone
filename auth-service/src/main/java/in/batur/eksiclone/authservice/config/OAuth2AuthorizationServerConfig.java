package in.batur.eksiclone.authservice.config;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import in.batur.eksiclone.authservice.util.KeyPairManager;

@Configuration
public class OAuth2AuthorizationServerConfig {

    @Autowired
    private KeyPairManager keyPairManager;
    
    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    @Order(1) // Daha yüksek öncelik (daha düşük sayı)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Belirli OAuth2 endpoint'lerine eşleştirme
        http.securityMatcher("/oauth2/**", "/.well-known/**", "/userinfo", "/token/**");
        
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        
        http.with(authorizationServerConfigurer, (oauth2) -> 
            oauth2.oidc(Customizer.withDefaults())  // OIDC 1.0 ayarları etkinleştir
        );
        
        return http
            .formLogin(Customizer.withDefaults())
            .build();
    }

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        RegisteredClient webClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("web-client")
            .clientSecret("{noop}web-client-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("http://localhost:3000/callback")
            .redirectUri("http://localhost:3000/silent-renew.html")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("read")
            .scope("write")
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMillis(jwtProperties.getExpiration()))
                .refreshTokenTimeToLive(Duration.ofMillis(jwtProperties.getRefreshExpiration()))
                .build())
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(true)
                .build())
            .build();

        RegisteredClient mobileClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("mobile-client")
            .clientSecret("{noop}mobile-client-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("com.eksiclone.app://oauth2/callback")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("read")
            .scope("write")
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMillis(jwtProperties.getExpiration()))
                .refreshTokenTimeToLive(Duration.ofMillis(jwtProperties.getRefreshExpiration()))
                .build())
            .clientSettings(ClientSettings.builder()
                .requireProofKey(true)  // PKCE etkinleştir
                .requireAuthorizationConsent(true)
                .build())
            .build();

        return new InMemoryRegisteredClientRepository(webClient, mobileClient);
    }

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = new RSAKey.Builder(keyPairManager.getPublicKey())
            .privateKey(keyPairManager.getPrivateKey())
            .keyID("eksiclone-key-id")
            .build();
        
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keyPairManager.getPublicKey()).build();
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .issuer("http://auth-service:8765")
            .build();
    }
}