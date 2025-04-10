package com.cbdg.interview.application.security;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    private Map<String, String> tokens = new HashMap<>();

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public boolean validateToken(String token) {
        return tokens.containsKey(token);
    }

    public String getUsernameFromToken(String token) {
        return tokens.get(token);
    }
}