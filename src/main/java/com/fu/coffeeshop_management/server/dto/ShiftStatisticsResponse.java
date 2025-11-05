package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for shift statistics (Manager dashboard)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftStatisticsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    
    private Integer totalShifts;
    private Integer openShifts;
    private Integer closedShifts;
    
    private BigDecimal totalCashHandled;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal totalRefunds;
    
    private BigDecimal averageShiftCash;
    private BigDecimal totalDiscrepancy;
    private BigDecimal largestDiscrepancy;
    
    private Long averageShiftDurationMinutes;
    private Integer totalTransactions;
}
