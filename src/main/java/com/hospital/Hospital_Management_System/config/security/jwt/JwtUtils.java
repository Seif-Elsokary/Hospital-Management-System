package com.hospital.Hospital_Management_System.config.security.jwt;

import com.hospital.Hospital_Management_System.config.security.userService.CustomerUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtUtils {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationTime}")
    private int expirationTime;

    public String generateToken(Authentication authentication) {
        CustomerUserDetails user = (CustomerUserDetails) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT Expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT Unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT Malformed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT Illegal Argument: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("JWT Signature invalid: {}", e.getMessage());
        }
        return false;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret); // ✅ هذا التعديل المهم
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
