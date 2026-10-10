
package com.homeservice.management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY =
            "HomeServiceManagementSecretKey2026ForJWT123456";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();
        String method = request.getMethod();

        // Login and authentication endpoints are public.
        if (path.startsWith("/api/auth/")) {
            return true;
        }

        // Allow public registration without processing a stale JWT.
        return "POST".equalsIgnoreCase(method)
                && "/api/users".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No bearer token: let Spring Security decide whether
        // this endpoint is public or requires authentication.
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(
                            Keys.hmacShaKeyFor(
                                    SECRET_KEY.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            )
                    )
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            if (email == null || email.isBlank()
                    || role == null || role.isBlank()) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(
                        "Invalid token: email or role is missing"
                );
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + role
                                    )
                            )
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(
                    "Invalid or expired token"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
