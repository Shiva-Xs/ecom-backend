package com.shiva.simpleWebApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple health-check endpoints.
 */
@Tag(name = "General", description = "Health check and general info endpoints")
@RestController
public class HomeController {

    @Operation(summary = "Health check", description = "Returns a welcome message to confirm the server is running.")
    @GetMapping("/health")
    public String health() {
        return "Welcome! The API is running. Visit /swagger-ui.html to explore all endpoints.";
    }

    @Operation(summary = "About", description = "Returns a short description of the application.")
    @GetMapping("/about")
    public String about() {
        return "Spring Boot Simple E-Commerce Backend — a beginner-friendly REST API demo.";
    }
}
