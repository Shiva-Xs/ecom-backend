package com.shiva.simpleWebApp.service;

import com.shiva.simpleWebApp.exception.ProductNotFoundException;
import com.shiva.simpleWebApp.model.Product;
import com.shiva.simpleWebApp.repository.ProductRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepo repo;

    public ProductService(ProductRepo repo) {
        this.repo = repo;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProduct(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            applyImageData(product, imageFile);
        }
        product.setAvailable(product.getStockQuantity() > 0);
        return repo.save(product);
    }

    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {
        Product existing = getProduct(id);
        product.setId(id);
        if (imageFile != null && !imageFile.isEmpty()) {
            applyImageData(product, imageFile);
        } else {
            product.setImageName(existing.getImageName());
            product.setImageType(existing.getImageType());
            product.setImageData(existing.getImageData());
        }
        product.setAvailable(product.getStockQuantity() > 0);
        return repo.save(product);
    }

    private void applyImageData(Product product, MultipartFile imageFile) throws IOException {
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed. Received: " + contentType);
        }
        String originalName = imageFile.getOriginalFilename();
        String safeName = (originalName != null)
                ? Paths.get(originalName).getFileName().toString()
                : null;
        product.setImageName(safeName);
        product.setImageType(contentType);
        product.setImageData(imageFile.getBytes());
    }

    public void deleteProduct(int id) {
        getProduct(id);
        repo.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        String escaped = keyword
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return repo.searchProducts(escaped);
    }
}
