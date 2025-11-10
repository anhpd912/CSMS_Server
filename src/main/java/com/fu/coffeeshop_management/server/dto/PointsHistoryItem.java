package com.fu.coffeeshop_management.server.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PointsHistoryItem(
        UUID transactionId,
        UUID orderId,
        String orderNo,
        String type,
        Integer pointsEarned,
        Integer pointsSpent,
        LocalDateTime timestamp
) {}