# Spring Boot E-Commerce Backend

> A clean, well-commented REST API for a product catalog, built layer by layer to show how a real Spring Boot backend is structured.

Every class explains *why* decisions were made, not just what the code does. If you're learning Java backend development, clone this, run it, and read through it. It's designed to be a reference as much as a project.


## What It Does

This is a basic CRUD API for managing a product catalog. You can create products, update them, delete them, fetch them by ID, search across multiple fields, and attach images. Everything goes through a REST interface with consistent JSON responses and meaningful error messages.

The moment you start the app, a full storefront UI opens at `http://localhost:8080` with no setup and no frontend toolchain required. Browse products, search in real time, add items to a cart, and manage the catalog through the same API the UI uses.

## Features

- **Full CRUD** with correct HTTP methods and status codes
- **Input validation** using field-level constraints (`@NotBlank`, `@DecimalMin`, `@Min`) with structured error responses that tell the client exactly which field failed and why
- **Multipart image upload** with the image served back as raw bytes with the correct `Content-Type`
- **Full-text search** across `name`, `brand`, `category`, and `description` in a single JPQL query, case-insensitive, with proper wildcard escaping
- **Global exception handling** so every error returns the same JSON shape and clients only need one error handler
- **Swagger UI** auto-generated from the controllers, no Postman required to explore the API
- **H2 in-memory database** with zero configuration, resets on every restart which keeps the development loop fast


## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.2 |
| Persistence | Spring Data JPA + Hibernate 6 |
| Database | H2 (in-memory) |
| Validation | Jakarta Validation via `spring-boot-starter-validation` |
| API docs | springdoc-openapi 2.5.0 |
| Boilerplate | Lombok |
| Build | Maven |


## Project Structure

```
ecom_backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/shiva/simpleWebApp/
    │   │   ├── Application.java
    │   │   ├── config/
    │   │   │   └── OpenApiConfig.java          # Swagger UI metadata
    │   │   ├── controller/
    │   │   │   ├── ProductController.java      # All product CRUD endpoints
    │   │   │   └── HomeController.java         # /health and /about
    │   │   ├── exception/
    │   │   │   ├── ProductNotFoundException.java
    │   │   │   ├── ErrorResponse.java          # Consistent error shape (record)
    │   │   │   └── GlobalExceptionHandler.java # @RestControllerAdvice
    │   │   ├── model/
    │   │   │   └── Product.java                # JPA entity with validation
    │   │   ├── repository/
    │   │   │   └── ProductRepo.java            # JpaRepository + custom JPQL
    │   │   └── service/
    │   │       └── ProductService.java         # Business logic + image handling
    │   └── resources/
    │       ├── application.properties          # local only, gitignored — copy from .example
    │       ├── application-default.properties  # local only, gitignored — dev-only config
    │       ├── application.properties.example  # committed — copy this to get started
    │       └── static/
    │           └── index.html                  # Frontend SPA (AI-assisted)
    └── test/
        └── .../service/
            └── ProductServiceTest.java         # Unit tests (Mockito)
```


## Getting Started

**Prerequisite:** Java 21+

```bash
# 1. Clone
git clone https://github.com/Shiva-Xs/ecom_backend.git
cd ecom_backend

# 2. Copy config
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties

# 3. Run
./mvnw spring-boot:run
```

Open **`http://localhost:8080`** and the storefront loads automatically.

Data lives in-memory. Every restart is a clean slate by design.


## API Reference

| Method | Endpoint | Description | Status |
|---|---|---|---|
| `GET` | `/api/products` | List all products | `200` |
| `GET` | `/api/products/{id}` | Get product by ID | `200` / `404` |
| `POST` | `/api/products` | Create product (multipart) | `201` / `400` |
| `PUT` | `/api/products/{id}` | Update product (multipart) | `200` / `400` / `404` |
| `DELETE` | `/api/products/{id}` | Delete product | `204` / `404` |
| `GET` | `/api/products/{id}/image` | Serve raw image bytes | `200` / `404` |
| `GET` | `/api/products/search?keyword=` | Full-text search | `200` / `400` |
| `GET` | `/health` | Health check | `200` |
| `GET` | `/about` | App description | `200` |


## Testing the API

Three options, pick your comfort level.

### Swagger UI (easiest)

Open **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Every endpoint is documented with its parameters, request body schema, and all possible response codes. Hit **Try it out** to fire real requests without leaving the browser.

Raw OpenAPI spec: `http://localhost:8080/v3/api-docs`

### Postman

1. Open Postman, click **Import**, and paste `http://localhost:8080/v3/api-docs`
2. Postman generates a full collection from the spec automatically
3. Set `baseUrl` to `http://localhost:8080` and start firing requests

For multipart endpoints (`POST /api/products`, `PUT /api/products/{id}`):
- Body type: **form-data**
- Key `product`, type **Text**: `{"name":"...","brand":"...","price":9.99,"category":"...","stockQuantity":10}`
- Key `imageFile`, type **File**: attach any image (optional)

### curl

```bash
# List all products
curl http://localhost:8080/api/products

# Create a product with an image
curl -X POST http://localhost:8080/api/products \
  -F 'product={"name":"Wireless Mouse","brand":"Logitech","price":29.99,"category":"Electronics","stockQuantity":50};type=application/json' \
  -F 'imageFile=@/path/to/image.jpg'

# Update a product
curl -X PUT http://localhost:8080/api/products/1 \
  -F 'product={"name":"Wireless Mouse Pro","brand":"Logitech","price":49.99,"category":"Electronics","stockQuantity":30};type=application/json'

# Search
curl "http://localhost:8080/api/products/search?keyword=logitech"

# Delete
curl -X DELETE http://localhost:8080/api/products/1
```


## Error Responses

Every error, whether a validation failure, a missing resource, or bad input, returns the same JSON shape so your client only needs one error handler:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 99",
  "timestamp": "2026-03-26T18:00:00.000Z"
}
```

Validation errors include a `fieldErrors` map with per-field messages:

```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields failed validation",
  "fieldErrors": {
    "name": "Product name is required",
    "price": "Price must be greater than 0"
  },
  "timestamp": "2026-03-26T18:00:00.000Z"
}
```

Handled cases: `400 Validation Failed` · `400 Bad Request` · `404 Not Found` · `413 Payload Too Large` · `500 Internal Server Error`


## What the Code Demonstrates

| Concept | Where to look |
|---|---|
| MVC layering | `controller/` to `service/` to `repository/` |
| JPA entity with Bean Validation | `model/Product.java` |
| Custom JPQL with wildcard escaping | `repository/ProductRepo.java` |
| Global exception handling | `exception/GlobalExceptionHandler.java` |
| Consistent error response shape | `exception/ErrorResponse.java` (Java record) |
| Multipart image upload with MIME validation | `service/ProductService.java` → `applyImageData()` |
| Path traversal prevention | `Paths.get(originalName).getFileName()` in `ProductService` |
| OpenAPI / Swagger setup | `config/OpenApiConfig.java` |
| Constructor injection throughout | Every `@Service` and `@RestController` |
| Unit tests with Mockito | `test/.../service/ProductServiceTest.java` |


## H2 Console

Inspect the live database at **`http://localhost:8080/h2-console`**

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(leave blank)* |


### Design Tradeoffs

To keep the project simple and focused on learning:

- Entities are returned directly instead of DTOs (would be separated in production)
- Images are stored in the database (production systems use object storage like S3)
- Search uses JPQL with LIKE (not optimized for large datasets)
- No authentication layer (would use Spring Security + JWT in production)

These tradeoffs keep the code easy to follow while still demonstrating real backend patterns.


## Room for Improvements

This project is intentionally scoped as a learning reference — a clean foundation for understanding Spring Boot's layered architecture. Here is what a production version would look like, roughly in order of impact.

### Replace H2 with PostgreSQL or MySQL

H2 is perfect for learning and demos, but data disappears on every restart and the SQL dialect differs from what real deployments use. Swapping to PostgreSQL is a one-line dependency change and a few property updates:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecom
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
```

### Move images to object storage (S3 / Cloudflare R2)

Storing image bytes directly in the database works for a demo, but it means every product query fetches potentially megabytes of binary data even when you only need names and prices. The production pattern is to upload images to object storage and store a URL string in the database. Cloudflare R2 has a generous free tier and an S3-compatible API.

### Add pagination to the products list

`GET /api/products` currently returns every row in the table. With enough products, a single request could pull large amounts of data. Spring Data JPA makes this straightforward:

```java
@GetMapping("/products")
public ResponseEntity<Page<Product>> getAllProducts(
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(service.getAllProducts(page, size));
}
```

### Add authentication with Spring Security and JWT

Right now any client can call `DELETE /api/products/1` and wipe the catalog. Adding Spring Security with stateless JWT tokens takes a few hours and transforms the project from a CRUD demo into something closer to production. The typical setup is: anyone can read products, only authenticated users with `ROLE_ADMIN` can write.

### Add `@Transactional` to write operations

`updateProduct` makes two database calls — a read and then a write. Without `@Transactional`, a failure between them leaves the database in a half-updated state. Annotating write methods with `@Transactional` and read methods with `@Transactional(readOnly = true)` is standard practice in any Spring application.

### Add controller-layer integration tests

The unit tests cover the service layer well, but nothing verifies that the HTTP layer wires up correctly. Status codes, validation error shapes, `Content-Type` headers, and how the controller handles a `ProductNotFoundException` are all untested. A handful of `@WebMvcTest` tests with `MockMvc` would close this gap.

### Add category filtering and sorting

The search endpoint does full-text matching but there is no way to filter by a specific category or sort by price or date. Both are straightforward additions using Spring Data derived query methods:

```java
List<Product> findByCategoryIgnoreCase(String category);
List<Product> findAllByOrderByPriceAsc();
```


