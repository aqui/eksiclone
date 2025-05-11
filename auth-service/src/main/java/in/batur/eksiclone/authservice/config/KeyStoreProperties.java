package in.batur.eksiclone.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt.key-store")
public class KeyStoreProperties {
    private String path = "./keys";
    private String privateKeyFile = "private.key";
    private String publicKeyFile = "public.key";
    
    // Getters and setters
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getPrivateKeyFile() {
        return privateKeyFile;
    }
    
    public void setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }
    
    public String getPublicKeyFile() {
        return publicKeyFile;
    }
    
    public void setPublicKeyFile(String publicKeyFile) {
        this.publicKeyFile = publicKeyFile;
    }
}