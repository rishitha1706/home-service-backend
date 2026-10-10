
package com.homeservice.management.config;

import com.homeservice.management.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // Stateless JWT API
            .csrf(csrf -> csrf.disable())

            // Enable CORS for the React frontend
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            // Do not create HTTP sessions
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Allow browser preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                // Public login and authentication endpoints
                .requestMatchers("/api/auth/**")
                .permitAll()

                // Public customer/provider registration
                .requestMatchers(HttpMethod.POST, "/api/users")
                .permitAll()

                // Public service list for signup and booking pages
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/services",
                    "/api/services/**"
                )
                .permitAll()

                // Admin endpoints
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                // Provider read endpoints require authentication
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/providers/**"
                )
                .authenticated()

                // Provider management endpoints
                .requestMatchers("/api/providers/**")
                .hasAnyRole("ADMIN", "SERVICE_PROVIDER")

                // Customer endpoints
                .requestMatchers("/api/customer/**")
                .hasRole("CUSTOMER")

                // All other endpoints require authentication
                .anyRequest()
                .authenticated()
            )

            // JWT authentication instead of form login
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

      
configuration.setAllowedOrigins(
    List.of(
        "http://localhost:5173",
        "https://home-service-frontend-0hg9.onrender.com"
    )
);


        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}
