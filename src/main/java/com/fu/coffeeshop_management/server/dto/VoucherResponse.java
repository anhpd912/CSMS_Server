package com.fu.coffeeshop_management.server.dto;

import com.fu.coffeeshop_management.server.entity.Voucher.VoucherStatus;
import com.fu.coffeeshop_management.server.entity.Voucher.VoucherType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class VoucherResponse {
    private UUID id;
    private String code;
    private VoucherType type;
    private BigDecimal value;
    private LocalDate startDate;
    private LocalDate endDate;
    private VoucherStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
