package com.shiva.simpleWebApp.repository;

import com.shiva.simpleWebApp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * ProductRepo extends JpaRepository, which gives us all standard CRUD methods
 * (findAll, findById, save, deleteById, etc.) for free — no implementation needed.
 *
 * We only need to define custom query methods here.
 */
public interface ProductRepo extends JpaRepository<Product, Integer> {

    /**
     * Custom JPQL query: searches across name, description, brand, and category.
     * LOWER() + CONCAT('%', :keyword, '%') makes the search case-insensitive
     * and matches any substring, not just exact values.
     *
     * Note: @Param("keyword") binds the method parameter to the :keyword placeholder.
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!' OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!' OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!' OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!'")
    List<Product> searchProducts(@Param("keyword") String keyword);
}
