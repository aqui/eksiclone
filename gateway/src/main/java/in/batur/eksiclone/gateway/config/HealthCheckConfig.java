package in.batur.eksiclone.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.net.URI;

@Configuration
public class HealthCheckConfig {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckConfig.class);

    // Fallback değerle birlikte tanımlayalım
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:8081/api/v1/auth/jwks.json}")
    private String jwkSetUri;
    
    @Value("${health.check.timeout-seconds:5}")
    private int timeoutSeconds;
    
    @Bean("healthWebClient")
    WebClient healthCheckWebClient() {
        return WebClient.builder()
                .build();
    }
    
    /**
     * JWT servisinin sağlık durumunu kontrol eder.
     * 
     * @return JWT servisi aktif ise true, değilse false
     */
    public boolean isJwtServiceHealthy() {
        try {
            if (jwkSetUri == null || jwkSetUri.isEmpty()) {
                logger.warn("JWK set URI is not configured. Health check will be skipped.");
                return true; // Yapılandırma yoksa başarılı kabul et
            }
            
            // JWK endpoint'inin URL'sini çıkar
            URI jwkUri = new URI(jwkSetUri);
            String jwkServiceBaseUrl = jwkUri.getScheme() + "://" + jwkUri.getHost();
            
            if (jwkUri.getPort() > 0) {
                jwkServiceBaseUrl += ":" + jwkUri.getPort();
            }
            
            String healthUrl = jwkServiceBaseUrl + "/api/v1/auth/health";
            logger.debug("Checking JWT service health at: {}", healthUrl);
            
            WebClient webClient = healthCheckWebClient();
            HttpStatusCode statusCode = webClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode())
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(e -> {
                        logger.warn("Health check failed: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .block();
            
            boolean isHealthy = statusCode != null && statusCode.is2xxSuccessful();
            if (isHealthy) {
                logger.debug("JWT service is healthy");
            } else {
                logger.warn("JWT service is unhealthy, status code: {}", statusCode);
            }
            
            return isHealthy;
        } catch (Exception e) {
            logger.error("Error checking JWT service health: {}", e.getMessage());
            return false;
        }
    }
}