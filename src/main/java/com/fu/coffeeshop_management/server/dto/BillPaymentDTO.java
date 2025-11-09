package com.fu.coffeeshop_management.server.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BillPaymentDTO {
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paidAt;
}
