package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.InventoryTransaction;
import com.fu.coffeeshop_management.server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
    
    /**
     * Find all transactions for a specific product
     */
    List<InventoryTransaction> findByProduct(Product product);
    
    /**
     * Find all transactions for a product ordered by time descending
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId ORDER BY it.transactionTime DESC")
    List<InventoryTransaction> findByProductIdOrderByTransactionTimeDesc(@Param("productId") UUID productId);
    
    /**
     * Find transactions within a date range
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionTime BETWEEN :startDate AND :endDate ORDER BY it.transactionTime DESC")
    List<InventoryTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find transactions by type and product
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId AND it.transactionType = :type ORDER BY it.transactionTime DESC")
    List<InventoryTransaction> findByProductIdAndType(@Param("productId") UUID productId, @Param("type") String type);
    
    /**
     * Count total incoming transactions for a product
     */
    @Query("SELECT COUNT(it) FROM InventoryTransaction it WHERE it.product.id = :productId AND it.transactionType = 'INCOMING'")
    Long countIncomingByProductId(@Param("productId") UUID productId);
}
