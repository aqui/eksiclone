package in.batur.eksiclone.gateway.config;

// ConfigurationProperties annotation'ını kaldırıp
public class JwtHealthProperties {
    private boolean enabled = true;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}