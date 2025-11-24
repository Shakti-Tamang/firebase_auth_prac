package com.example.firebase_auth.config;


import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    public static final String CUSTOM_BEARER = "X-Access-Token";

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0")
                        .description("API documentation for Auth Service")
                        .contact(new Contact()
                                .name("Shakti Support")
                                .email("tamangshakti423@gmail.com")))
                // Add security requirement globally
                .addSecurityItem(new SecurityRequirement().addList(CUSTOM_BEARER))
                .components(new Components()
                        .addSecuritySchemes(CUSTOM_BEARER,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Access-Token")  // your header name
                        ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Auth Setup")
                .packagesToScan("com.example.firebase_authcontroller")
                .pathsToMatch("/api/**")
                .build();
    }
}


