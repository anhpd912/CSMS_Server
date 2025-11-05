package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for recording incoming stock transactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomingStockRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity cannot exceed 10000")
    private Integer quantity;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "INCOMING|OUTGOING|ADJUSTMENT", message = "Transaction type must be INCOMING, OUTGOING, or ADJUSTMENT")
    private String transactionType;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
