package in.batur.eksiclone.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JWT token'larının önbellek yönetimini sağlayan sınıf.
 * Yapılandırma özelliklerini kullanarak token'ların önbellekte ne kadar süre 
 * tutulacağını ve ne zaman yenileneceğini belirler.
 */
public class JwtCacheManager {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtCacheManager.class);
    private final WebClient webClient;
    private final GatewayJwtProperties jwtProperties;
    private final ConcurrentHashMap<String, CachedToken> tokenCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    private static class CachedToken {
        private final String token;
        private final long expirationTime;
        
        public CachedToken(String token, long expirationTimeMillis) {
            this.token = token;
            this.expirationTime = System.currentTimeMillis() + expirationTimeMillis;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
        
        public String getToken() {
            return token;
        }
    }
    
    public JwtCacheManager(@Qualifier("jwtWebClient") WebClient webClient, GatewayJwtProperties jwtProperties) {
        this.webClient = webClient;
        this.jwtProperties = jwtProperties;
        
        // TTL ve refresh değerlerini kullanma
        long refreshMillis = parseDuration(jwtProperties.getRefresh()).toMillis();
        
        // Periyodik önbellek temizleme işlemi başlat
        scheduler.scheduleAtFixedRate(this::cleanExpiredTokens, refreshMillis, refreshMillis, TimeUnit.MILLISECONDS);
        
        logger.info("JWT Cache TTL: {}", parseDuration(jwtProperties.getTtl()));
        logger.info("JWT Cache Refresh: {}", parseDuration(jwtProperties.getRefresh()));
    }
    
    /**
     * String formatındaki süre değerini (örn. "5m", "30s") Duration nesnesine dönüştürür.
     */
    private Duration parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return Duration.ofMinutes(5); // Varsayılan değer
        }
        
        char unit = durationStr.charAt(durationStr.length() - 1);
        String value = durationStr.substring(0, durationStr.length() - 1);
        
        try {
            long timeValue = Long.parseLong(value);
            
            switch (unit) {
                case 's':
                    return Duration.ofSeconds(timeValue);
                case 'm':
                    return Duration.ofMinutes(timeValue);
                case 'h':
                    return Duration.ofHours(timeValue);
                case 'd':
                    return Duration.ofDays(timeValue);
                default:
                    // Birim belirtilmemişse dakika olarak kabul et
                    return Duration.ofMinutes(timeValue);
            }
        } catch (NumberFormatException e) {
            return Duration.ofMinutes(5); // Hata durumunda varsayılan değer
        }
    }
    
    /**
     * Süresi dolmuş token'ları temizler.
     */
    private void cleanExpiredTokens() {
        tokenCache.forEach((key, value) -> {
            if (value.isExpired()) {
                tokenCache.remove(key);
            }
        });
    }
    
    /**
     * JWT token'ını önbellekten alır veya yeni bir token alır.
     */
    public Mono<String> getJwtToken(String subject) {
        CachedToken cachedToken = tokenCache.get(subject);
        
        if (cachedToken != null && !cachedToken.isExpired()) {
            return Mono.just(cachedToken.getToken());
        }
        
        return fetchNewToken(subject);
    }
    
    /**
     * Yeni bir JWT token'ı alır.
     */
    private Mono<String> fetchNewToken(String subject) {
        Duration ttl = parseDuration(jwtProperties.getTtl());
        
        // WebClient kullanarak JWT servisten yeni token alma
        return webClient.get()
                .uri(builder -> builder
                        .path("/api/v1/auth/token")
                        .queryParam("subject", subject)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(token -> {
                    // Yeni token'ı önbelleğe ekle
                    tokenCache.put(subject, new CachedToken(token, ttl.toMillis()));
                })
                .onErrorResume(e -> {
                    logger.error("Error fetching JWT token for {}: {}", subject, e.getMessage());
                    // Eğer token servisine erişilemiyorsa, boş Mono dön
                    return Mono.empty();
                });
    }
    
    /**
     * Önbelleği tamamen temizler.
     */
    public void clearCache() {
        tokenCache.clear();
    }
    
    /**
     * Belirli bir subject için önbelleği temizler.
     */
    public void clearCache(String subject) {
        tokenCache.remove(subject);
    }
}