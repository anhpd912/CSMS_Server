package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
