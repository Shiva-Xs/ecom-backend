package com.shiva.simpleWebApp.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response body returned by the GlobalExceptionHandler.
 * Having a consistent error shape makes it much easier for frontend developers
 * to handle errors reliably.
 *
 * fieldErrors is only populated for validation failures (400); it is omitted
 * from the JSON output for all other error types.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) Map<String, String> fieldErrors,
        Instant timestamp
) {}
