package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.service.BillService;
import com.fu.coffeeshop_management.server.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;
    private final LoyaltyService loyaltyService;

    @PostMapping("/bills/calculate")
    public ResponseEntity<BillCalculationResponse> calculateBill(@RequestBody BillGenerationRequest request) {
        BillCalculationResponse calculation = billService.calculateBill(request);
        return ResponseEntity.ok(calculation);
    }

    @PostMapping("/bills/generate")
    public ResponseEntity<BillResponse> generateBill(@RequestBody BillGenerationRequest request) {
        BillResponse bill = billService.generateBill(request);
        return new ResponseEntity<>(bill, HttpStatus.CREATED);
    }

    @GetMapping("/bills/{billId}")
    public ResponseEntity<BillDetailResponse> getBillDetails(@PathVariable UUID billId) {
        BillDetailResponse billDetails = billService.getBillDetails(billId);
        return ResponseEntity.ok(billDetails);
    }

    @GetMapping("/bills")
    public ResponseEntity<List<BillSummaryDTO>> getBillList(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        List<BillSummaryDTO> bills = billService.getBillList(date);
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/bills/{billId}/confirm-payment")
    public ResponseEntity<PaymentConfirmationResponse> confirmPayment(
            @PathVariable UUID billId,
            @Valid @RequestBody PaymentConfirmationRequest request) {

        PaymentConfirmationResponse response = billService.confirmPayment(billId, request);
        return ResponseEntity.ok(response);
    }
}
