package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByTimeDTO {
    private String timeLabel;
    private BigDecimal totalRevenue;
    private Long billCount;
}
