package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.CreateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.UpdateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.VoucherListItem;
import com.fu.coffeeshop_management.server.dto.VoucherResponse;
import com.fu.coffeeshop_management.server.entity.Voucher;
import com.fu.coffeeshop_management.server.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

    private final VoucherService service;

    public VoucherController(VoucherService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VoucherResponse> create(@Valid @RequestBody CreateVoucherRequest req,
                                                  Authentication auth) {
        String actor = (auth != null ? auth.getName() : "SYSTEM");
        VoucherResponse res = service.create(req, actor);
        return ResponseEntity
                .created(URI.create("/api/v1/vouchers/" + res.getId()))
                .body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VoucherResponse> patch(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateVoucherRequest req,
                                                 Authentication auth) {
        String actor = (auth != null ? auth.getName() : "SYSTEM");
        return ResponseEntity.ok(service.patch(id, req, actor));
    }

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