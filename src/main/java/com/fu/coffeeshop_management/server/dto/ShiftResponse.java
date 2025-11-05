package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for shift response
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponse {

    private UUID id;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal openingCash;
    private BigDecimal closingCash;
    private String status; // OPEN, CLOSED
    private Long durationMinutes;
    private BigDecimal cashDiscrepancy; // Difference between expected and actual closing cash
}
