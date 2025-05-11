package in.batur.eksiclone.security.controller;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import in.batur.eksiclone.authservice.util.KeyPairManager;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/auth")
public class JwkSetController {

    @Autowired
    private KeyPairManager keyPairManager;
    
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
    
    @GetMapping("/jwks-health") // Change the path
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}