package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>{
    /**
     * Finds all products belonging to a specific category by its ID.
     */
    List<Product> findByCategoryId(UUID categoryId);
    
    /**
     * Finds all products by category name and status.
     * This is the key method we'll use to fetch the customer menu, e.g.,
     * findByCategoryNameAndStatus("Coffee Drinks", "active")
     */
    List<Product> findByCategoryNameAndStatus(String categoryName, String status);

    /**
     * Finds all products by their status (e.g., "active" or "inactive").
     */
    List<Product> findByStatus(String status);
    
    /**
     * Finds a product by ID with its category and stock eagerly loaded
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.stock WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndStock(@Param("id") UUID id);
    
    /**
     * Finds all ingredients (products with category name "Ingredient")
     */
    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.name = 'Ingredient' AND p.status = :status")
    List<Product> findIngredientsByStatus(@Param("status") String status);
    
    /**
     * Search ingredients by name (case-insensitive)
     */
    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.name = 'Ingredient' AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchIngredientsByName(@Param("name") String name);
}
