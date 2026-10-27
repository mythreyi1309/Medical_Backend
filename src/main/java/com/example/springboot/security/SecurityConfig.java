package com.example.springboot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"message\":\"Authentication required or token invalid/expired\"}");
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(403);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"message\":\"Access denied for your role\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth

                // CORS preflight + Spring's error dispatch must never be blocked
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error").permitAll()

                // Admin and doctor login
                .requestMatchers(
                    HttpMethod.POST, "/auth/login"
                ).permitAll()

                // Patient login
                .requestMatchers(
                    HttpMethod.POST, "/patient/login"
                ).permitAll()

                // Patient registration
                .requestMatchers(
                    HttpMethod.POST, "/patient"
                ).permitAll()

                // Only ADMIN and DOCTOR can view patients
                .requestMatchers(
                    HttpMethod.GET, "/patient"
                ).hasAnyRole("ADMIN", "DOCTOR")

                // Only ADMIN can view doctors
                .requestMatchers(
                    HttpMethod.GET, "/doctor"
                ).hasRole("ADMIN")

                // Only ADMIN can add doctors
                .requestMatchers(
                    HttpMethod.POST, "/doctor"
                ).hasRole("ADMIN")

                // Only ADMIN can delete doctors
                .requestMatchers(
                    HttpMethod.DELETE, "/doctor/**"
                ).hasRole("ADMIN")

                // Only ADMIN and DOCTOR can access adherence reports
                .requestMatchers(
                    "/adherence-report/**"
                ).hasAnyRole("ADMIN", "DOCTOR")

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
