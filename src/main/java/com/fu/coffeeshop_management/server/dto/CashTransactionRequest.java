package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for recording cash transactions during a shift
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashTransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotEmpty(message = "Transaction type is required")
    private String transactionType; // CASH_IN, CASH_OUT, REFUND

    private String description;

    private String referenceNumber; // Order ID, Bill ID, etc.
}
