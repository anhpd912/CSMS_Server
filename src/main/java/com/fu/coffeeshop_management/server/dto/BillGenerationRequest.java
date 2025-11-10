package com.fu.coffeeshop_management.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BillGenerationRequest {
    private UUID orderId;
    private String customerPhone;
    private String voucherCode;
    private int pointsToRedeem;
}
