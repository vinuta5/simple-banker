package com.cbdg.interview.application.config;

import no.nav.security.mock.oauth2.MockOAuth2Server;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@TestConfiguration
public class TestSecurityConfig {

    public static final String ISSUER = "test-issuer";
    private final MockOAuth2Server mockOAuth2Server;

    public TestSecurityConfig() {
        this.mockOAuth2Server = new MockOAuth2Server();
        this.mockOAuth2Server.start();
    }

    @Bean
    @Primary
    public MockOAuth2Server mockOAuth2Server() {
        return mockOAuth2Server;
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(mockOAuth2Server.jwksUrl(ISSUER).toString()).build();
    }
}