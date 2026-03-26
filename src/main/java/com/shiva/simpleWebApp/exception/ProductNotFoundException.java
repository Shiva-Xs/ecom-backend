package com.shiva.simpleWebApp.exception;

/**
 * Thrown when a product is not found in the database.
 * The GlobalExceptionHandler catches this and returns a clean 404 response
 * instead of letting Spring produce a generic 500 error page.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
