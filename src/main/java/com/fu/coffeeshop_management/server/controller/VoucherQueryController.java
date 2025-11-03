package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.VoucherListItem;
import com.fu.coffeeshop_management.server.entity.Voucher;
import com.fu.coffeeshop_management.server.service.VoucherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherQueryController {

    private final VoucherQueryService service;

    /**
     * Filters:
     * - type: PERCENT|FIXED_AMOUNT (chọn 1)
     * - status: ACTIVE|INACTIVE (chọn 1)
     * - code: substring (search bar)
     * Sort:
     * - sortBy: startDate | endDate (mặc định startDate), luôn ASC
     * No pagination: trả về List
     */
    @GetMapping
    public List<VoucherListItem> list(
            @RequestParam(required = false) Voucher.VoucherType type,
            @RequestParam(required = false) Voucher.VoucherStatus status,
            @RequestParam(required = false) String code,
            @RequestParam(required = false, defaultValue = "startDate") String sortBy
    ) {
        return service.list(code, status, type, sortBy);
    }

}
