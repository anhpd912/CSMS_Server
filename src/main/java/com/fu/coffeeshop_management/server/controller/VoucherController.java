package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.CreateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.VoucherResponse;
import com.fu.coffeeshop_management.server.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
}