package com.shiva.simpleWebApp.service;

import com.shiva.simpleWebApp.exception.ProductNotFoundException;
import com.shiva.simpleWebApp.model.Product;
import com.shiva.simpleWebApp.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepo repo;

    @InjectMocks
    private ProductService service;

    // ── getProduct ──────────────────────────────────────────────────────────

    @Test
    void getProduct_throwsProductNotFoundException_whenNotFound() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.getProduct(99));
    }

    @Test
    void getProduct_returnsProduct_whenFound() {
        Product product = buildProduct(1);
        when(repo.findById(1)).thenReturn(Optional.of(product));
        assertEquals(product, service.getProduct(1));
    }

    // ── addProduct ──────────────────────────────────────────────────────────

    @Test
    void addProduct_setsAvailableTrue_whenStockIsPositive() throws IOException {
        Product product = buildProduct(null);
        product.setStockQuantity(5);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(service.addProduct(product, null).isAvailable());
    }

    @Test
    void addProduct_setsAvailableFalse_whenStockIsZero() throws IOException {
        Product product = buildProduct(null);
        product.setStockQuantity(0);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        assertFalse(service.addProduct(product, null).isAvailable());
    }

    @Test
    void addProduct_throwsIllegalArgument_whenNonImageFileUploaded() {
        Product product = buildProduct(null);
        MockMultipartFile htmlFile = new MockMultipartFile(
                "imageFile", "exploit.html", "text/html", "<script>alert(1)</script>".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.addProduct(product, htmlFile));
    }

    @Test
    void addProduct_acceptsValidImageFile() throws IOException {
        Product product = buildProduct(null);
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Product result = service.addProduct(product, imageFile);
        assertEquals("photo.jpg", result.getImageName());
        assertEquals("image/jpeg", result.getImageType());
    }

    // ── deleteProduct ───────────────────────────────────────────────────────

    @Test
    void deleteProduct_throwsProductNotFoundException_whenNotFound() {
        when(repo.findById(55)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.deleteProduct(55));
    }

    @Test
    void deleteProduct_callsDeleteById_whenFound() {
        Product product = buildProduct(1);
        when(repo.findById(1)).thenReturn(Optional.of(product));
        service.deleteProduct(1);
        verify(repo).deleteById(1);
    }

    // ── searchProducts ──────────────────────────────────────────────────────

    @Test
    void searchProducts_escapesPercentWildcard() {
        when(repo.searchProducts(any())).thenReturn(List.of());
        service.searchProducts("%");
        verify(repo).searchProducts("!%");
    }

    @Test
    void searchProducts_escapesUnderscoreWildcard() {
        when(repo.searchProducts(any())).thenReturn(List.of());
        service.searchProducts("_");
        verify(repo).searchProducts("!_");
    }

    @Test
    void searchProducts_escapesExclamationMark() {
        when(repo.searchProducts(any())).thenReturn(List.of());
        service.searchProducts("!");
        verify(repo).searchProducts("!!");
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Product buildProduct(Integer id) {
        Product p = new Product();
        p.setId(id);
        p.setName("Test Product");
        p.setBrand("TestBrand");
        p.setCategory("TestCategory");
        p.setPrice(BigDecimal.valueOf(9.99));
        return p;
    }
}
