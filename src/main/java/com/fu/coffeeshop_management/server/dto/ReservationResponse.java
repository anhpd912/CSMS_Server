package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private UUID id;
    private String customerName;
    private String customerPhone;
    private LocalDateTime reservationTime;
    private Integer numGuests;
    private String status;
    private LocalDateTime createdAt;
    private UUID tableId; // Only include the ID for TableInfo
    private UUID userId;  // Only include the ID for User
}
