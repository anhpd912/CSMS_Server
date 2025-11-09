package com.fu.coffeeshop_management.server.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BillDetailResponse {

    private UUID billId;
    private UUID orderId;
    private LocalDateTime issuedTime;
    private String paymentStatus;

    private String tableName;
    private String cashierName;

    private BillCustomerDTO customerInfo;

    private List<BillItemDTO> items;

    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal finalAmount;

    private String voucherCode;
    private int pointsRedeemed;

    private List<BillPaymentDTO> payments;
}


