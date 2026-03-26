package com.shiva.simpleWebApp.controller;

import com.shiva.simpleWebApp.exception.ErrorResponse;
import com.shiva.simpleWebApp.exception.ProductNotFoundException;
import com.shiva.simpleWebApp.model.Product;
import com.shiva.simpleWebApp.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Products", description = "Endpoints for managing the product catalog")
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ── GET all products ───────────────────────────────────────────────────────

    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "List of products returned successfully")
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }

    // ── GET single product ─────────────────────────────────────────────────────

    @Operation(summary = "Get a product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        return new ResponseEntity<>(service.getProduct(id), HttpStatus.OK);
    }

    // ── POST new product ───────────────────────────────────────────────────────

    @Operation(summary = "Add a new product", description = "Accepts multipart/form-data with a JSON product part and an image file part.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed — check response body for field-level errors")
    })
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @Valid @RequestPart Product product,
            @RequestPart(required = false) MultipartFile imageFile) throws IOException {
        return new ResponseEntity<>(service.addProduct(product, imageFile), HttpStatus.CREATED);
    }

    // ── PUT update product ─────────────────────────────────────────────────────

    @Operation(summary = "Update an existing product", description = "The ID in the path determines which product is updated.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable int id,
            @Valid @RequestPart Product product,
            @RequestPart(required = false) MultipartFile imageFile) throws IOException {
        return new ResponseEntity<>(service.updateProduct(id, product, imageFile), HttpStatus.OK);
    }

    // ── DELETE product ─────────────────────────────────────────────────────────

    @Operation(summary = "Delete a product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ── GET product image ──────────────────────────────────────────────────────

    @Operation(summary = "Get product image", description = "Returns the raw image bytes with the correct Content-Type.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Image returned"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{id}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int id) {
        Product product = service.getProduct(id);
        if (product.getImageType() == null || product.getImageData() == null) {
            throw new ProductNotFoundException("No image found for product with id: " + id);
        }
        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(product.getImageType());
        } catch (InvalidMediaTypeException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(product.getImageData());
    }

    // ── GET search ─────────────────────────────────────────────────────────────

    @Operation(summary = "Search products", description = "Case-insensitive substring search across name, description, brand, and category.")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        return new ResponseEntity<>(service.searchProducts(keyword), HttpStatus.OK);
    }
}
