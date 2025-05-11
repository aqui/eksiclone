package in.batur.eksiclone.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HealthCheckConfig {

    @Bean("healthWebClient")
    WebClient healthCheckWebClient() {
        return WebClient.builder().build();
    }
}