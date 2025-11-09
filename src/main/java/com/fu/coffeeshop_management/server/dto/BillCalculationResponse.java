package com.fu.coffeeshop_management.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BillCalculationResponse {
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal finalTotal;
    private String customerName;
    private int customerAvailablePoints;
}
