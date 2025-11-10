package com.fu.coffeeshop_management.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateLoyaltyMemberRequest(
        String fullName,
        String phone,
        String email
) {}