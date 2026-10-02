package com.ptc.halo.security;

import com.ptc.halo.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // extractUsername verifies the signature and expiry through JwtService's parser.
                String email = jwtService.extractUsername(header.substring(7));
                if (email == null || email.isBlank()) throw new IllegalArgumentException("Missing subject");
                UserDetails details = userDetailsService.loadUserByUsername(email);
                if (!details.isEnabled() || !details.isAccountNonLocked() || !details.isAccountNonExpired()) {
                    ApiSecurityErrors.write(request, response, 403, "ACCOUNT_INACTIVE");
                    return;
                }
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
            } catch (JwtException | IllegalArgumentException | AuthenticationException invalid) {
                SecurityContextHolder.clearContext();
                ApiSecurityErrors.write(request, response, 401, "INVALID_OR_EXPIRED_TOKEN");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
