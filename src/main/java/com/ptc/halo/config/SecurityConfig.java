package com.ptc.halo.config;

import com.ptc.halo.security.JwtAuthenticationFilter;
import com.ptc.halo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());


        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register/student"
                        ).permitAll()

                        .requestMatchers("/api/super-admin/**")
                        .hasRole("SUPER_ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/professor/**")
                        .hasRole("PROFESSOR")

                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}