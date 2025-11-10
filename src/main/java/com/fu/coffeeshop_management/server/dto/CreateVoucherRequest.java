package com.fu.coffeeshop_management.server.dto;

import com.fu.coffeeshop_management.server.entity.Voucher.VoucherStatus;
import com.fu.coffeeshop_management.server.entity.Voucher.VoucherType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateVoucherRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9_\\-]{3,32}$",
            message = "code chỉ gồm A–Z, 0–9, _ hoặc -, 3–32 ký tự")
    private String code;

    @NotNull
    private VoucherType type;

    @NotNull
    @Digits(integer = 18, fraction = 2)
    @DecimalMin(value = "0.00", inclusive = false, message = "value phải > 0")
    private BigDecimal value;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private VoucherStatus status;

}
