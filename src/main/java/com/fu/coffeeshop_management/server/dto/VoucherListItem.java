// src/main/java/com/fu/coffeeshop_management/server/controller/voucher/dto/VoucherListItem.java
package com.fu.coffeeshop_management.server.dto;

import com.fu.coffeeshop_management.server.entity.Voucher.VoucherStatus;
import com.fu.coffeeshop_management.server.entity.Voucher.VoucherType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VoucherListItem(
        UUID id,
        String code,
        VoucherType type,
        BigDecimal discountValue,
        LocalDate startDate,
        LocalDate endDate,
        VoucherStatus status
) {}
