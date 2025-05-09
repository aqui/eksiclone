package in.batur.eksiclone.security.filter;

import in.batur.eksiclone.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            
            if (jwt != null) {
                try {
                    if (jwtService.validateToken(jwt)) {
                        String username = jwtService.getUsernameFromToken(jwt);
                        
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authenticationToken = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.debug("JWT doğrulama başarılı: {}", username);
                    }
                } catch (ExpiredJwtException e) {
                    logger.error("JWT süresi doldu: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"expired_token\",\"message\":\"JWT süresi doldu\"}");
                    return;
                } catch (MalformedJwtException e) {
                    logger.error("Geçersiz JWT formatı: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"invalid_token\",\"message\":\"Geçersiz JWT formatı\"}");
                    return;
                } catch (SignatureException e) {
                    logger.error("Geçersiz JWT imzası: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"invalid_signature\",\"message\":\"Geçersiz JWT imzası\"}");
                    return;
                } catch (UnsupportedJwtException e) {
                    logger.error("Desteklenmeyen JWT: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"unsupported_token\",\"message\":\"Desteklenmeyen JWT\"}");
                    return;
                } catch (Exception e) {
                    logger.error("JWT doğrulama hatası: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"authentication_error\",\"message\":\"JWT doğrulama hatası\"}");
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Kullanıcı kimlik doğrulaması ayarlanamıyor: {}", e.getMessage());
            // Güvenlik nedeniyle isteğin devam etmesine izin verelim, ama kimlik doğrulaması olmadan
        }
        
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
}