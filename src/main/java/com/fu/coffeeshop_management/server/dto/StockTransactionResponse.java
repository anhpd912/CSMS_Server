package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning inventory transaction information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionResponse {

    private UUID id;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private String transactionType;
    private LocalDateTime transactionTime;
    private String userName;
    private Integer stockLevelAfter;
}
