package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for starting a new shift
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartShiftRequest {

    @NotNull(message = "Opening cash amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Opening cash must be zero or positive")
    private BigDecimal openingCash;
}
