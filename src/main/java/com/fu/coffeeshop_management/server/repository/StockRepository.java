package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID>{
    /**
     * Finds all stock items where the quantity in stock is less than or
     * equal to the reorder level. This is for low-stock alerts.
     */
    @Query("SELECT s FROM Stock s WHERE s.quantityInStock <= s.reorderLevel")
    List<Stock> findLowStockItems();
}
