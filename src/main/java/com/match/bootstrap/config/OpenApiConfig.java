package com.match.bootstrap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI matchOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Match API").version("v1")
                .description("Tinder-benzeri sosyal platform API (Hexagonal)"))
            .addSecurityItem(new SecurityRequirement().addList("bearer"))
            .components(new Components().addSecuritySchemes("bearer",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}

