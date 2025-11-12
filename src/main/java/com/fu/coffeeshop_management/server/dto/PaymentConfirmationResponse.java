package com.fu.coffeeshop_management.server.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class PaymentConfirmationResponse {
    private UUID billId;
    private UUID paymentId;
    private String newStatus;
    private String message;
    private int pointsEarned;
    private int pointsSpent;
}
