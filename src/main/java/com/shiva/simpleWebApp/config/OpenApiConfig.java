package com.shiva.simpleWebApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * springdoc-openapi automatically scans all @RestController classes and generates
 * API documentation from them. This config class adds metadata (title, description,
 * version, contact) that appears at the top of the Swagger UI page.
 *
 * Once the app is running, visit:
 *   Swagger UI (interactive): http://localhost:8080/swagger-ui.html
 *   Raw JSON spec:            http://localhost:8080/v3/api-docs
 *
 * The Swagger UI lets you try every endpoint directly in the browser —
 * no Postman or curl required.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simple E-Commerce API")
                        .version("1.0.0")
                        .description("""
                                A beginner-friendly Spring Boot REST API for a product catalog.

                                Concepts demonstrated:
                                - Full CRUD with correct HTTP methods and status codes
                                - Input validation with field-level error messages
                                - Global exception handling with consistent JSON error responses
                                - Multipart image upload stored directly on the entity
                                - Custom JPQL search across name, brand, category, and description
                                - H2 in-memory database — zero config, data resets on restart
                                """)
                        .contact(new Contact()
                                .name("GitHub — Shiva-Xs")
                                .url("https://github.com/Shiva-Xs/ecom_backend")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")
                ));
    }
}
