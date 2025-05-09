package in.batur.eksiclone.gateway.config;

// ConfigurationProperties annotation'ını tamamen kaldırın
public class GatewayJwtProperties {
    private String ttl = "5m";
    private String refresh = "1m";
    
    // Getters and setters
    public String getTtl() {
        return ttl;
    }
    
    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
    
    public String getRefresh() {
        return refresh;
    }
    
    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }
}