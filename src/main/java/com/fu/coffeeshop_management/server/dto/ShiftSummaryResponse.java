package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for comprehensive shift summary report
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftSummaryResponse {

    private UUID shiftId;
    private String cashierName;
    private String cashierEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;
    private String status;

    // Cash Summary
    private BigDecimal openingCash;
    private BigDecimal closingCash;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal totalRefunds;
    private BigDecimal expectedClosingCash;
    private BigDecimal cashDiscrepancy;

    // Transaction Summary
    private Integer totalTransactions;
    private Integer cashInCount;
    private Integer cashOutCount;
    private Integer refundCount;

    // Transaction Details
    private List<CashTransactionResponse> transactions;
}
