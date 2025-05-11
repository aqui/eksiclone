package in.batur.eksiclone.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    // Make JwtDecoder optional to allow the gateway to start without auth-service
    @Autowired(required = false)
    private JwtDecoder jwtDecoder;
    
    // Whitelist of paths that don't need authentication
    private static final String[] AUTH_WHITELIST = {
        "/api/v1/auth/**",
        "/actuator/**",
        "/v3/api-docs/**",
        "/swagger-ui/**", 
        "/oauth2/**"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // If JwtDecoder is not available yet, allow the request but log it
        if (jwtDecoder == null) {
            logger.warn("JwtDecoder not available, bypassing authentication for: {}", path);
            return chain.filter(exchange);
        }
        
        // Whitelist check
        for (String permittedPath : AUTH_WHITELIST) {
            if (path.startsWith(permittedPath.replace("/**", ""))) {
                return chain.filter(exchange);
            }
        }
        
        // Authorization header check
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        try {
            // JWT token validation
            jwtDecoder.decode(token);
            return chain.filter(exchange);
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // Filter order priority
    }
}