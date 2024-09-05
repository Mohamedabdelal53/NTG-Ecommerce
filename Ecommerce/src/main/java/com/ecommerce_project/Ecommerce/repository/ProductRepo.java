package com.ecommerce_project.Ecommerce.repository;

import com.ecommerce_project.Ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    boolean existsByNameAndDescription(String name, String description);

    // Search by name or description
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // More advanced query for multiple keywords
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword1, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword1, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword1, '%')) " +
            "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword2, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword2, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword2, '%'))")
    List<Product> searchByMultipleKeywords(@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);
}
