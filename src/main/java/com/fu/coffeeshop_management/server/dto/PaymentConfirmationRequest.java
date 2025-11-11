package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentConfirmationRequest {

    @NotBlank(message = "Payment method is required.")
    private String paymentMethod;
}


