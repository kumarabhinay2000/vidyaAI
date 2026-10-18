package org.vidyaai.userservices.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Configuring OpenAPI documentation");
        try {
            final String securitySchemeName = "bearerAuth";

            return new OpenAPI()
                    .info(new Info()
                            .title("VidyaAI User Service API")
                            .description("User Management and Authentication Service for VidyaAI Platform")
                            .version("1.0.0")
                            .contact(new Contact()
                                    .name("VidyaAI Team")
                                    .email("support@vidyaai.com")
                                    .url("https://vidyaai.com"))
                            .license(new License()
                                    .name("Apache 2.0")
                                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
                    .servers(List.of(
                            new Server().url("http://localhost:8081").description("Local Server"),
                            new Server().url("http://localhost:8080/api").description("API Gateway")
                    ))
                    .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                    .components(new Components()
                            .addSecuritySchemes(securitySchemeName,
                                    new SecurityScheme()
                                            .name(securitySchemeName)
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .description("Enter JWT token")));
        } catch (Exception e) {
            log.error("Error configuring OpenAPI: {}", e.getMessage(), e);
            throw e;
        }
    }
}