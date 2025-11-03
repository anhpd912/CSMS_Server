package com.fu.coffeeshop_management.server.controller;
import com.fu.coffeeshop_management.server.dto.UpdateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.VoucherResponse;
import com.fu.coffeeshop_management.server.service.VoucherCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherCommandController {

    private final VoucherCommandService service;

    @PatchMapping("/{id}")
    public ResponseEntity<VoucherResponse> patch(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateVoucherRequest req,
                                                 Authentication auth) {
        String actor = (auth != null ? auth.getName() : "SYSTEM");
        return ResponseEntity.ok(service.patch(id, req, actor));
    }

}