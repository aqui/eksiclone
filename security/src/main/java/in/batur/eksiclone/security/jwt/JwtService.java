package in.batur.eksiclone.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import in.batur.eksiclone.security.config.JwtProperties;
import in.batur.eksiclone.security.util.KeyPairManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final KeyPairManager keyPairManager;
    private final JwtProperties jwtProperties;

    public JwtService(KeyPairManager keyPairManager, JwtProperties jwtProperties) {
        this.keyPairManager = keyPairManager;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(jwtProperties.getExpiration(), ChronoUnit.MILLIS)))
                .signWith(keyPairManager.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(jwtProperties.getRefreshExpiration(), ChronoUnit.MILLIS)))
                .signWith(keyPairManager.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(keyPairManager.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(keyPairManager.getPublicKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Geçersiz JWT imzası: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Geçersiz JWT token: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token süresi doldu: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Desteklenmeyen JWT token: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT token boş veya null: {}", e.getMessage());
            throw e;
        }
    }
}