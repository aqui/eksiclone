package in.batur.eksiclone.gateway.config;

import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class JwtConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtConfigurer.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:8081/api/v1/auth/jwks.json}")
    private String jwkSetUri;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.connection-timeout:5000}")
    private int connectionTimeout;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.read-timeout:5000}")
    private int readTimeout;

    @Bean
    @Primary
    ReactiveJwtDecoder reactiveJwtDecoder() {
        try {
            logger.info("Creating JWT decoder with JWK Set URI: {}", jwkSetUri);
            
            // HTTP client'ı bağlantı ve okuma zaman aşımları ile yapılandırıyoruz
            HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));
            
            // WebClient oluşturuyoruz
            WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
                
            // NimbusReactiveJwtDecoder oluşturuyoruz ve RS256 algoritmasını desteklemesini sağlıyoruz
            // WebClient'ı doğrudan decoder oluşturma aşamasında kullanıyoruz
            NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .webClient(webClient)  // WebClient'ı burada ekliyoruz
                .jwsAlgorithms(algorithms -> {
                    algorithms.add(SignatureAlgorithm.RS256); // Sadece RS256 algoritmasını destekle
                })
                .build();
            
            logger.info("JWT decoder successfully created");
            return jwtDecoder;
        } catch (Exception e) {
            logger.error("Error creating JWT decoder: {}", e.getMessage(), e);
            throw new RuntimeException("Could not create JWT decoder", e);
        }
    }
    
    // JWK endpoint sağlık kontrolü için kullanılacak WebClient
    @Bean("jwtHealthWebClient")
    WebClient jwtHealthWebClient() {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout))))
            .build();
    }
}