package com.hospital.Hospital_Management_System.config.security.jwt;

import com.hospital.Hospital_Management_System.config.security.userService.CustomerUserDetailsService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseJwt(request);
            if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
                String email = jwtUtils.generateUserNameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            handleException(response, "Token expired: " + e.getMessage());
            return;
        } catch (UnsupportedJwtException e) {
            handleException(response, "Unsupported JWT: " + e.getMessage());
            return;
        } catch (MalformedJwtException e) {
            handleException(response, "Malformed JWT: " + e.getMessage());
            return;
        } catch (SignatureException e) {
            handleException(response, "Invalid signature: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            handleException(response, "Illegal argument: " + e.getMessage());
            return;
        } catch (Exception e) {
            handleException(response, "Unknown error: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
