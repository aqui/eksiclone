package in.batur.eksiclone.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReactiveJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    private static final Logger log = LoggerFactory.getLogger(ReactiveJwtAuthenticationConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> claims = jwt.getClaims();
        
        if (claims.containsKey("roles")) {
            try {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("roles");
                
                if (roles == null || roles.isEmpty()) {
                    log.warn("JWT içerisinde roller listesi boş: {}", jwt.getSubject());
                    throw new BadCredentialsException("JWT içinde geçerli yetki bulunamadı");
                }
                
                authorities = roles.stream()
                        .filter(role -> role != null && !role.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                // Rol sayısını kontrol et
                if (authorities.isEmpty()) {
                    log.warn("JWT içerisinde geçerli rol bulunamadı: {}", jwt.getSubject());
                    throw new BadCredentialsException("JWT içinde geçerli yetki bulunamadı");
                }
                
            } catch (ClassCastException e) {
                log.error("JWT'den rolleri ayrıştırırken hata: {}", e.getMessage());
                throw new BadCredentialsException("JWT'den yetki bilgileri çıkarılırken hata oluştu", e);
            }
        } else {
            log.warn("JWT içinde 'roles' alanı bulunamadı: {}", jwt.getSubject());
            throw new BadCredentialsException("JWT içinde 'roles' alanı bulunamadı");
        }
        
        return authorities;
    }
}