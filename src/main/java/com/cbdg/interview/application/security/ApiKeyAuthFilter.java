package com.cbdg.interview.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String apiKey;

    public ApiKeyAuthFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestApiKey = request.getHeader("Authorization");

        if (apiKey.equals(requestApiKey)) {
            List<SimpleGrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority("SCOPE_read"),
                    new SimpleGrantedAuthority("SCOPE_write")
            );
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("API", null, authorities));
        }

        filterChain.doFilter(request, response);
    }
}