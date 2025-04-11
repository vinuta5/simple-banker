package com.cbdg.interview.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "OAuth", description = "OAuth token endpoints")
public class MockOAuthController {

    private static final Logger logger = LoggerFactory.getLogger(MockOAuthController.class);
    private static final String EXPECTED_CLIENT_ID = "mock-client-id";
    private static final String EXPECTED_CLIENT_SECRET = "mock-client-secret";

    @Operation(summary = "Get OAuth token", description = "Generates a mock OAuth token for testing purposes")
    @PostMapping("/oauth2/token")
    public ResponseEntity<Map<String, Object>> getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret) {

        logger.info("Received token request - grant_type: {}, client_id: {}", grantType, clientId);

        // Validate grant type, client ID, and client secret
        if (!"client_credentials".equals(grantType)
                || !EXPECTED_CLIENT_ID.equals(clientId)
                || !EXPECTED_CLIENT_SECRET.equals(clientSecret)) {
            logger.warn("Invalid client credentials or grant type");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "invalid_client");
            errorResponse.put("error_description", "Invalid client credentials");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "mocked-access-token-" + System.currentTimeMillis());
        tokenResponse.put("token_type", "bearer");
        tokenResponse.put("expires_in", 3600);
        tokenResponse.put("scope", "read write");

        logger.info("Token generated successfully");
        return ResponseEntity.ok(tokenResponse);
    }
}