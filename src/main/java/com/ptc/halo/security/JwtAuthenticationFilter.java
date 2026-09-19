package com.ptc.halo.security;

import com.ptc.halo.entity.UserSessionEntity;
import com.ptc.halo.enums.SessionStatus;
import com.ptc.halo.repository.UserSessionRepository;
import com.ptc.halo.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserSessionRepository userSessionRepository;


    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService, UserSessionRepository userSessionRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        String token = authHeader.substring(7);


        try {

            String email =
                    jwtService.extractUsername(token);

            String sessionId =
                    jwtService.extractSessionId(token);

            if (email != null &&
                    sessionId != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserSessionEntity session =
                        userSessionRepository
                                .findBySessionId(sessionId)
                                .orElse(null);

                if (session == null) {

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );
                    return;
                }

                if (session.getStatus()
                        != SessionStatus.ACTIVE) {

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );
                    return;
                }

                LocalDateTime now =
                        LocalDateTime.now();

                if (session.getExpiresAt()
                        .isBefore(now)) {

                    session.setStatus(
                            SessionStatus.EXPIRED
                    );

                    userSessionRepository.save(session);

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );
                    return;
                }

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                session.setLastActivity(now);

                userSessionRepository.save(session);
            }

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}