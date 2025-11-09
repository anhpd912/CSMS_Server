package com.fu.coffeeshop_management.server.dto;

import java.util.UUID;

public record LoyaltyMemberDetailResponse(
        UUID customerId,
        String fullName,
        String phone,
        String email,
        UUID loyaltyId,
        Integer points,
        String tier
) {}