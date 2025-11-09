package com.fu.coffeeshop_management.server.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID; /**
 * DTO đầu ra (Response) cho API xác nhận thanh toán (UC-0404 Main Flow Step 7)
 */
@Data
@Builder
public class PaymentConfirmationResponse {
    private UUID billId;
    private UUID paymentId;
    private String newStatus; // "Paid"
    private String message; // "Payment successful."
    private int pointsEarned; // (Thông tin thêm cho Step 6)
    private int pointsSpent;
}
