package in.batur.eksiclone.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HealthCheckConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    
    @Bean("healthWebClient")
    WebClient healthCheckWebClient() {
        return WebClient.builder().build();
    }
    
    // Sağlık kontrolü için kullanılacak metod
    public boolean isJwtServiceHealthy() {
        try {
            // JWK endpoint'inin URL'sini çıkar
            String jwkServiceBaseUrl = jwkSetUri.substring(0, jwkSetUri.lastIndexOf('/'));
            String healthUrl = jwkServiceBaseUrl + "/health";
            
            WebClient webClient = healthCheckWebClient();
            HttpStatusCode statusCode = webClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode())
                    .block();
            
            return statusCode != null && statusCode.is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}