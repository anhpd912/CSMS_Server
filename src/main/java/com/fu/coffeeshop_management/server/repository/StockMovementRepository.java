package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    /**
     * Finds all stock movements for a specific product.
     */
    List<StockMovement> findByProductId(UUID productId);

    /**
     * Finds stock movements for a product within a specific date range.
     */
    List<StockMovement> findByProductIdAndTransactionTimeBetween(UUID productId, LocalDateTime start, LocalDateTime end);
}
