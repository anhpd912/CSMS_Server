package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemReportDetailDTO {
    private String itemName;
    private Long totalUnit;
    private BigDecimal totalRevenue;
}
