package com.fu.coffeeshop_management.server.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BillItemDTO {
    private String productName;
    private int quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal lineTotal;
}
