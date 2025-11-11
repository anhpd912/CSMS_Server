package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDetailDTO {

    private LocalDate date;
    private BigDecimal totalRevenue;
    private Long billCount;
}