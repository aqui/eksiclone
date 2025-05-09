package in.batur.eksiclone.userservice.controller;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import in.batur.eksiclone.security.util.KeyPairManager;

@RestController
@RequestMapping("/api/v1/auth")
public class UserServiceJwkSetController {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceJwkSetController.class);

    @Autowired
    private KeyPairManager keyPairManager;
    
    @GetMapping("/jwks.json")
    public ResponseEntity<Map<String, Object>> keys() {
        try {
            logger.info("JWKS endpoint called");
            
            RSAPublicKey publicKey = keyPairManager.getPublicKey();
            if (publicKey == null) {
                logger.error("Public key is null");
                return ResponseEntity.status(500).body(Map.of("error", "Public key is null"));
            }
            
            RSAKey key = new RSAKey.Builder(publicKey)
                    .keyID("eksiclone-key-id")
                    .build();
            
            JWKSet jwkSet = new JWKSet(key);
            Map<String, Object> jwks = jwkSet.toJSONObject();
            
            logger.info("JWKS response generated successfully");
            
            // Cache control configuration
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                    .body(jwks);
        } catch (Exception e) {
            logger.error("Error generating JWKS response", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "JWKS generation error: " + e.getMessage()));
        }
    }
}