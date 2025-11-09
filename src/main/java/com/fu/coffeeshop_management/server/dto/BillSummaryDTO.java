package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillSummaryDTO {


    private UUID billId;
    private LocalDateTime issuedTime;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String cashierName;
    private String paymentMethod;
}