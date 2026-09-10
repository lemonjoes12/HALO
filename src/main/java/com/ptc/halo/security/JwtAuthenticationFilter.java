package com.ptc.halo.security;

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


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");


        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request,response);
            return;
        }


        String token = authHeader.substring(7);


        String email = jwtService.extractUsername(token);


        if(email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {


            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );


            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }


        filterChain.doFilter(request,response);

    }
}