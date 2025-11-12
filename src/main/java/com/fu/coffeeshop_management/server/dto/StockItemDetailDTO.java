package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemDetailDTO {
    private String itemName;
    private String unit;
    private Integer stockQuantity;
}