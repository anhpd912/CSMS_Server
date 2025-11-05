package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for low stock notification items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockResponse {

    private UUID productId;
    private String productName;
    private Integer currentStock;
    private Integer reorderLevel;
    private Integer quantityNeeded;
    private BigDecimal estimatedCost;
    private String status;
}
