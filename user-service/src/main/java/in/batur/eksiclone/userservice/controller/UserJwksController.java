package in.batur.eksiclone.userservice.controller;

import in.batur.eksiclone.security.util.KeyPairManager;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWKS (JSON Web Key Set) kontrolcüsü.
 * JWT doğrulama için gerekli olan açık anahtar bilgisini sağlar.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class UserJwksController {

    private final KeyPairManager keyPairManager;
    
    public UserJwksController(KeyPairManager keyPairManager) {
        this.keyPairManager = keyPairManager;
    }
    
    /**
     * JWKS (JSON Web Key Set) bilgisini sağlayan endpoint.
     * Bu endpoint, JWT'lerin doğrulanması için gerekli olan açık anahtar bilgisini sunar.
     * 
     * @return JWKS bilgisi içeren yanıt
     */
    @GetMapping("/jwks.json")
    public ResponseEntity<Map<String, Object>> keys() {
        RSAPublicKey publicKey = keyPairManager.getPublicKey();
        RSAKey key = new RSAKey.Builder(publicKey)
                .keyID("eksiclone-key-id")
                .build();
        
        JWKSet jwkSet = new JWKSet(key);
        Map<String, Object> jwks = jwkSet.toJSONObject();
        
        // Cache'leme yapılandırması ekle
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .body(jwks);
    }
}