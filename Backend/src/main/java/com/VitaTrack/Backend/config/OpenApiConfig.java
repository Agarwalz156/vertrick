package com.VitaTrack.Backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI microHealthOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MicroHealth API").version("0.1").description("APIs for MicroHealth prototype"))
                .externalDocs(new ExternalDocumentation().description("Project docs"));
    }
}
