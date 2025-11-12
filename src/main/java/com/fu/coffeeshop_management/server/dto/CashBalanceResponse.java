package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashBalanceResponse {

    private BigDecimal openingCash;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal totalRefunds;
    private BigDecimal expectedBalance;
    private BigDecimal currentBalance;
    private Integer transactionCount;
}
