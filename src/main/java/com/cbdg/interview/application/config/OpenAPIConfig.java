package com.cbdg.interview.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Simple Banker API").version("1.0.0"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("api_key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("api_key"));
    }
}