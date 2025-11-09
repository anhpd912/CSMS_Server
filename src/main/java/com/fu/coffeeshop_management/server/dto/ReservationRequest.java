package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private String customerName;
    private String customerPhone;
    private LocalDateTime reservationTime;
    private Integer numGuests;
    private String status;
    private UUID tableId;
    private UUID userId;
}
