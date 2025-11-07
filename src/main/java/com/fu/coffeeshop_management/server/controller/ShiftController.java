package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller for handling shift management endpoints
 * Based on the Coffee Shop Management System requirements
 * 
 * Features:
 * - Start Shift (Cashier)
 * - End Shift (Cashier)
 * - Cash Tracking (Cashier & Manager)
 * - Shift History View (Manager)
 */
@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    /**
     * Start a new shift
     * Cashier records opening cash amount
     */
    @PostMapping("/start")
    public ResponseEntity<ShiftResponse> startShift(
            @Valid @RequestBody StartShiftRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ShiftResponse response = shiftService.startShift(currentUser, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current active shift for the authenticated user
     */
    @GetMapping("/current")
    public ResponseEntity<ShiftResponse> getCurrentShift(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ShiftResponse response = shiftService.getCurrentShift(currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * End the current active shift
     * Cashier records closing cash amount
     */
    @PostMapping("/end")
    public ResponseEntity<ShiftResponse> endShift(
            @Valid @RequestBody EndShiftRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ShiftResponse response = shiftService.endShift(currentUser, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Record a cash transaction during active shift
     * Transaction types: CASH_IN, CASH_OUT, REFUND
     */
    @PostMapping("/cash/record")
    public ResponseEntity<CashTransactionResponse> recordCashTransaction(
            @Valid @RequestBody CashTransactionRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        CashTransactionResponse response = shiftService.recordCashTransaction(currentUser, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current cash balance for active shift
     */
    @GetMapping("/cash/balance")
    public ResponseEntity<CashBalanceResponse> getCurrentCashBalance(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        CashBalanceResponse response = shiftService.getCurrentCashBalance(currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all cash transactions for a specific shift
     */
    @GetMapping("/{shiftId}/cash-transactions")
    public ResponseEntity<List<CashTransactionResponse>> getCashTransactionsForShift(
            @PathVariable UUID shiftId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<CashTransactionResponse> transactions = shiftService.getCashTransactionsForShift(shiftId, currentUser);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get shift by ID
     */
    @GetMapping("/{shiftId}")
    public ResponseEntity<ShiftResponse> getShiftById(
            @PathVariable UUID shiftId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ShiftResponse response = shiftService.getShiftById(shiftId, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Get comprehensive shift summary report
     * Includes all transactions, cash flow, and discrepancies
     */
    @GetMapping("/{shiftId}/summary")
    public ResponseEntity<ShiftSummaryResponse> getShiftSummary(
            @PathVariable UUID shiftId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ShiftSummaryResponse response = shiftService.getShiftSummary(shiftId, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all shifts with filters (Manager only)
     * Supports filtering by status, user, and date range
     * Includes pagination
     */
    @GetMapping
    public ResponseEntity<Page<ShiftResponse>> getAllShifts(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Page<ShiftResponse> shifts = shiftService.getAllShifts(status, userId, startDate, endDate, page, size, currentUser);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Get current user's shift history
     * Cashier can view their own shifts
     */
    @GetMapping("/my-history")
    public ResponseEntity<Page<ShiftResponse>> getMyShiftHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Page<ShiftResponse> shifts = shiftService.getMyShiftHistory(currentUser, page, size);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Get shift statistics for a date range (Manager only)
     * Provides aggregated data for reporting
     */
    @GetMapping("/statistics")
    public ResponseEntity<ShiftStatisticsResponse> getShiftStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID userId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ShiftStatisticsResponse response = shiftService.getShiftStatistics(startDate, endDate, userId, currentUser);
        return ResponseEntity.ok(response);
    }
}
