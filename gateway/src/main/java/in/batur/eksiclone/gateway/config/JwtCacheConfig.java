package in.batur.eksiclone.gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class JwtCacheConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.cache.ttl:5m}")
    private String ttl;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.cache.refresh:1m}")
    private String refresh;

    @Bean
    GatewayJwtProperties gatewayJwtProperties() {
        GatewayJwtProperties properties = new GatewayJwtProperties();
        properties.setTtl(ttl);
        properties.setRefresh(refresh);
        return properties;
    }

    @Bean
    JwtCacheManager jwtCacheManager(@Qualifier("jwtWebClient") WebClient webClient) {
        return new JwtCacheManager(webClient, gatewayJwtProperties());
    }
    
    @Bean("jwtWebClient")
    WebClient webClientForJwt() {
        return WebClient.builder().build();
    }
}