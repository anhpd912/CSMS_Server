package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for cash transaction response
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashTransactionResponse {

    private UUID id;
    private UUID shiftId;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private String referenceNumber;
    private LocalDateTime timestamp;
    private BigDecimal runningBalance;
}
