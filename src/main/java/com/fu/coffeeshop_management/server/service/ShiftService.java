package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.CashTransaction;
import com.fu.coffeeshop_management.server.entity.Shift;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.CashTransactionRepository;
import com.fu.coffeeshop_management.server.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final CashTransactionRepository cashTransactionRepository;

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final String TRANSACTION_CASH_IN = "CASH_IN";
    private static final String TRANSACTION_CASH_OUT = "CASH_OUT";
    private static final String TRANSACTION_REFUND = "REFUND";

    /**
     * Start a new shift for the current user
     */
    @Transactional
    public ShiftResponse startShift(User currentUser, StartShiftRequest request) {
        // Check if user already has an open shift
        shiftRepository.findByUserIdAndStatus(currentUser.getId(), STATUS_OPEN)
            .ifPresent(shift -> {
                throw new IllegalStateException("You already have an active shift. Please end it before starting a new one.");
            });

        Shift shift = Shift.builder()
            .user(currentUser)
            .startTime(LocalDateTime.now())
            .openingCash(request.getOpeningCash())
            .status(STATUS_OPEN)
            .build();

        Shift savedShift = shiftRepository.save(shift);
        return mapToShiftResponse(savedShift);
    }

    /**
     * Get current active shift for the user
     */
    public ShiftResponse getCurrentShift(User currentUser) {
        Shift shift = shiftRepository.findByUserIdAndStatus(currentUser.getId(), STATUS_OPEN)
            .orElseThrow(() -> new IllegalStateException("No active shift found. Please start a shift first."));
        
        return mapToShiftResponse(shift);
    }

    /**
     * End the current active shift
     */
    @Transactional
    public ShiftResponse endShift(User currentUser, EndShiftRequest request) {
        Shift shift = shiftRepository.findByUserIdAndStatus(currentUser.getId(), STATUS_OPEN)
            .orElseThrow(() -> new IllegalStateException("No active shift found to end."));

        shift.setEndTime(LocalDateTime.now());
        shift.setClosingCash(request.getClosingCash());
        shift.setStatus(STATUS_CLOSED);

        Shift savedShift = shiftRepository.save(shift);
        return mapToShiftResponse(savedShift);
    }

    /**
     * Record a cash transaction during active shift
     */
    @Transactional
    public CashTransactionResponse recordCashTransaction(User currentUser, CashTransactionRequest request) {
        Shift shift = shiftRepository.findByUserIdAndStatus(currentUser.getId(), STATUS_OPEN)
            .orElseThrow(() -> new IllegalStateException("No active shift found. Please start a shift first."));

        // Validate transaction type
        String transactionType = request.getTransactionType().toUpperCase();
        if (!List.of(TRANSACTION_CASH_IN, TRANSACTION_CASH_OUT, TRANSACTION_REFUND).contains(transactionType)) {
            throw new IllegalArgumentException("Invalid transaction type. Must be CASH_IN, CASH_OUT, or REFUND.");
        }

        // Calculate running balance
        BigDecimal currentBalance = calculateCurrentBalance(shift.getId());
        BigDecimal newBalance = currentBalance;

        if (TRANSACTION_CASH_IN.equals(transactionType)) {
            newBalance = currentBalance.add(request.getAmount());
        } else if (TRANSACTION_CASH_OUT.equals(transactionType) || TRANSACTION_REFUND.equals(transactionType)) {
            newBalance = currentBalance.subtract(request.getAmount());
        }

        CashTransaction transaction = CashTransaction.builder()
            .shift(shift)
            .amount(request.getAmount())
            .transactionType(transactionType)
            .description(request.getDescription())
            .referenceNumber(request.getReferenceNumber())
            .timestamp(LocalDateTime.now())
            .runningBalance(newBalance)
            .build();

        CashTransaction savedTransaction = cashTransactionRepository.save(transaction);
        return mapToCashTransactionResponse(savedTransaction);
    }

    /**
     * Get current cash balance for active shift
     */
    public CashBalanceResponse getCurrentCashBalance(User currentUser) {
        Shift shift = shiftRepository.findByUserIdAndStatus(currentUser.getId(), STATUS_OPEN)
            .orElseThrow(() -> new IllegalStateException("No active shift found."));

        return calculateCashBalance(shift.getId());
    }

    /**
     * Get all cash transactions for a specific shift
     */
    public List<CashTransactionResponse> getCashTransactionsForShift(UUID shiftId, User currentUser) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Shift not found with ID: " + shiftId));

        // Authorization check: User can only view their own shifts unless they're a manager
        if (!shift.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().getName().equals("MANAGER")) {
            throw new IllegalArgumentException("You don't have permission to view this shift.");
        }

        List<CashTransaction> transactions = cashTransactionRepository.findByShiftIdOrderByTimestampAsc(shiftId);
        return transactions.stream()
            .map(this::mapToCashTransactionResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get shift by ID
     */
    public ShiftResponse getShiftById(UUID shiftId, User currentUser) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Shift not found with ID: " + shiftId));

        // Authorization check
        if (!shift.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().getName().equals("MANAGER")) {
            throw new IllegalArgumentException("You don't have permission to view this shift.");
        }

        return mapToShiftResponse(shift);
    }

    /**
     * Get comprehensive shift summary report
     */
    public ShiftSummaryResponse getShiftSummary(UUID shiftId, User currentUser) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Shift not found with ID: " + shiftId));

        // Authorization check
        if (!shift.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().getName().equals("MANAGER")) {
            throw new IllegalArgumentException("You don't have permission to view this shift.");
        }

        CashBalanceResponse cashBalance = calculateCashBalance(shiftId);
        List<CashTransaction> transactions = cashTransactionRepository.findByShiftIdOrderByTimestampAsc(shiftId);

        long cashInCount = cashTransactionRepository.countByShiftIdAndType(shiftId, TRANSACTION_CASH_IN);
        long cashOutCount = cashTransactionRepository.countByShiftIdAndType(shiftId, TRANSACTION_CASH_OUT);
        long refundCount = cashTransactionRepository.countByShiftIdAndType(shiftId, TRANSACTION_REFUND);

        Long durationMinutes = null;
        BigDecimal cashDiscrepancy = null;
        BigDecimal expectedClosing = null;

        if (shift.getEndTime() != null) {
            durationMinutes = Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
            expectedClosing = shift.getOpeningCash()
                .add(cashBalance.getTotalCashIn())
                .subtract(cashBalance.getTotalCashOut())
                .subtract(cashBalance.getTotalRefunds());
            cashDiscrepancy = shift.getClosingCash().subtract(expectedClosing);
        }

        return ShiftSummaryResponse.builder()
            .shiftId(shift.getId())
            .cashierName(shift.getUser().getFullname())
            .cashierEmail(shift.getUser().getEmail())
            .startTime(shift.getStartTime())
            .endTime(shift.getEndTime())
            .durationMinutes(durationMinutes)
            .status(shift.getStatus())
            .openingCash(shift.getOpeningCash())
            .closingCash(shift.getClosingCash())
            .totalCashIn(cashBalance.getTotalCashIn())
            .totalCashOut(cashBalance.getTotalCashOut())
            .totalRefunds(cashBalance.getTotalRefunds())
            .expectedClosingCash(expectedClosing)
            .cashDiscrepancy(cashDiscrepancy)
            .totalTransactions(cashBalance.getTransactionCount())
            .cashInCount((int) cashInCount)
            .cashOutCount((int) cashOutCount)
            .refundCount((int) refundCount)
            .transactions(transactions.stream()
                .map(this::mapToCashTransactionResponse)
                .collect(Collectors.toList()))
            .build();
    }

    /**
     * Get paginated shifts with filters (Manager view)
     */
    public Page<ShiftResponse> getAllShifts(String status, UUID userId, LocalDate startDate, LocalDate endDate, 
                                           int page, int size, User currentUser) {
        // Only managers can view all shifts
        if (!currentUser.getRole().getName().equals("MANAGER")) {
            throw new IllegalArgumentException("Only managers can view all shifts.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Shift> shifts;

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        if (startDateTime != null && endDateTime != null) {
            if (status != null && !status.equalsIgnoreCase("all")) {
                shifts = shiftRepository.findByStatusAndDateRange(status.toUpperCase(), startDateTime, endDateTime, pageable);
            } else if (userId != null) {
                shifts = shiftRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime, pageable);
            } else {
                shifts = shiftRepository.findByDateRange(startDateTime, endDateTime, pageable);
            }
        } else if (status != null && !status.equalsIgnoreCase("all")) {
            if (userId != null) {
                shifts = shiftRepository.findByUserIdAndStatusOrderByStartTimeDesc(userId, status.toUpperCase(), pageable);
            } else {
                shifts = shiftRepository.findByStatusOrderByStartTimeDesc(status.toUpperCase(), pageable);
            }
        } else if (userId != null) {
            shifts = shiftRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);
        } else {
            shifts = shiftRepository.findAllByOrderByStartTimeDesc(pageable);
        }

        return shifts.map(this::mapToShiftResponse);
    }

    /**
     * Get user's own shift history
     */
    public Page<ShiftResponse> getMyShiftHistory(User currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Shift> shifts = shiftRepository.findByUserIdOrderByStartTimeDesc(currentUser.getId(), pageable);
        return shifts.map(this::mapToShiftResponse);
    }

    /**
     * Get shift statistics for a date range (Manager only)
     */
    public ShiftStatisticsResponse getShiftStatistics(LocalDate startDate, LocalDate endDate, UUID userId, User currentUser) {
        // Only managers can view statistics
        if (!currentUser.getRole().getName().equals("MANAGER")) {
            throw new IllegalArgumentException("Only managers can view shift statistics.");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Shift> shiftsPage;

        if (userId != null) {
            shiftsPage = shiftRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime, pageable);
        } else {
            shiftsPage = shiftRepository.findByDateRange(startDateTime, endDateTime, pageable);
        }

        List<Shift> shifts = shiftsPage.getContent();
        
        long openShifts = shiftRepository.countByStatusAndDateRange(STATUS_OPEN, startDateTime, endDateTime);
        long closedShifts = shiftRepository.countByStatusAndDateRange(STATUS_CLOSED, startDateTime, endDateTime);

        BigDecimal totalCashIn = BigDecimal.ZERO;
        BigDecimal totalCashOut = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        BigDecimal totalDiscrepancy = BigDecimal.ZERO;
        BigDecimal largestDiscrepancy = BigDecimal.ZERO;
        long totalDurationMinutes = 0;
        int totalTransactions = 0;
        int closedShiftCount = 0;

        for (Shift shift : shifts) {
            BigDecimal cashIn = cashTransactionRepository.sumAmountByShiftIdAndType(shift.getId(), TRANSACTION_CASH_IN);
            BigDecimal cashOut = cashTransactionRepository.sumAmountByShiftIdAndType(shift.getId(), TRANSACTION_CASH_OUT);
            BigDecimal refunds = cashTransactionRepository.sumAmountByShiftIdAndType(shift.getId(), TRANSACTION_REFUND);
            long transactionCount = cashTransactionRepository.countByShiftId(shift.getId());

            totalCashIn = totalCashIn.add(cashIn);
            totalCashOut = totalCashOut.add(cashOut);
            totalRefunds = totalRefunds.add(refunds);
            totalTransactions += transactionCount;

            if (shift.getStatus().equals(STATUS_CLOSED) && shift.getEndTime() != null) {
                closedShiftCount++;
                long duration = Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
                totalDurationMinutes += duration;

                BigDecimal expectedClosing = shift.getOpeningCash().add(cashIn).subtract(cashOut).subtract(refunds);
                BigDecimal discrepancy = shift.getClosingCash().subtract(expectedClosing).abs();
                totalDiscrepancy = totalDiscrepancy.add(discrepancy);

                if (discrepancy.compareTo(largestDiscrepancy) > 0) {
                    largestDiscrepancy = discrepancy;
                }
            }
        }

        BigDecimal totalCashHandled = totalCashIn.add(totalCashOut).add(totalRefunds);
        BigDecimal averageShiftCash = closedShiftCount > 0 ? 
            totalCashHandled.divide(BigDecimal.valueOf(closedShiftCount), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
        Long averageDuration = closedShiftCount > 0 ? totalDurationMinutes / closedShiftCount : 0L;

        return ShiftStatisticsResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalShifts(shifts.size())
            .openShifts((int) openShifts)
            .closedShifts((int) closedShifts)
            .totalCashHandled(totalCashHandled)
            .totalCashIn(totalCashIn)
            .totalCashOut(totalCashOut)
            .totalRefunds(totalRefunds)
            .averageShiftCash(averageShiftCash)
            .totalDiscrepancy(totalDiscrepancy)
            .largestDiscrepancy(largestDiscrepancy)
            .averageShiftDurationMinutes(averageDuration)
            .totalTransactions(totalTransactions)
            .build();
    }

    // Helper methods

    private BigDecimal calculateCurrentBalance(UUID shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        BigDecimal totalCashIn = cashTransactionRepository.sumAmountByShiftIdAndType(shiftId, TRANSACTION_CASH_IN);
        BigDecimal totalCashOut = cashTransactionRepository.sumAmountByShiftIdAndType(shiftId, TRANSACTION_CASH_OUT);
        BigDecimal totalRefunds = cashTransactionRepository.sumAmountByShiftIdAndType(shiftId, TRANSACTION_REFUND);

        return shift.getOpeningCash()
            .add(totalCashIn)
            .subtract(totalCashOut)
            .subtract(totalRefunds);
    }

    private CashBalanceResponse calculateCashBalance(UUID shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        BigDecimal totalCashIn = cashTransactionRepository.sumAmountByShiftIdAndType(shiftId, TRANSACTION_CASH_IN);
        BigDecimal totalCashOut = cashTransactionRepository.sumAmountByShiftIdAndType(shiftId, TRANSACTION_CASH_OUT);
        BigDecimal totalRefunds = cashTransactionRepository.sumAmountByShiftIdAndType(shiftId, TRANSACTION_REFUND);
        long transactionCount = cashTransactionRepository.countByShiftId(shiftId);

        BigDecimal expectedBalance = shift.getOpeningCash()
            .add(totalCashIn)
            .subtract(totalCashOut)
            .subtract(totalRefunds);

        return CashBalanceResponse.builder()
            .openingCash(shift.getOpeningCash())
            .totalCashIn(totalCashIn)
            .totalCashOut(totalCashOut)
            .totalRefunds(totalRefunds)
            .expectedBalance(expectedBalance)
            .currentBalance(expectedBalance)
            .transactionCount((int) transactionCount)
            .build();
    }

    private ShiftResponse mapToShiftResponse(Shift shift) {
        Long durationMinutes = null;
        BigDecimal cashDiscrepancy = null;

        if (shift.getEndTime() != null) {
            durationMinutes = Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
            
            if (shift.getClosingCash() != null) {
                BigDecimal totalCashIn = cashTransactionRepository.sumAmountByShiftIdAndType(shift.getId(), TRANSACTION_CASH_IN);
                BigDecimal totalCashOut = cashTransactionRepository.sumAmountByShiftIdAndType(shift.getId(), TRANSACTION_CASH_OUT);
                BigDecimal totalRefunds = cashTransactionRepository.sumAmountByShiftIdAndType(shift.getId(), TRANSACTION_REFUND);
                
                BigDecimal expectedClosing = shift.getOpeningCash()
                    .add(totalCashIn)
                    .subtract(totalCashOut)
                    .subtract(totalRefunds);
                cashDiscrepancy = shift.getClosingCash().subtract(expectedClosing);
            }
        }

        return ShiftResponse.builder()
            .id(shift.getId())
            .userId(shift.getUser().getId())
            .userFullName(shift.getUser().getFullname())
            .userEmail(shift.getUser().getEmail())
            .startTime(shift.getStartTime())
            .endTime(shift.getEndTime())
            .openingCash(shift.getOpeningCash())
            .closingCash(shift.getClosingCash())
            .status(shift.getStatus())
            .durationMinutes(durationMinutes)
            .cashDiscrepancy(cashDiscrepancy)
            .build();
    }

    private CashTransactionResponse mapToCashTransactionResponse(CashTransaction transaction) {
        return CashTransactionResponse.builder()
            .id(transaction.getId())
            .shiftId(transaction.getShift().getId())
            .amount(transaction.getAmount())
            .transactionType(transaction.getTransactionType())
            .description(transaction.getDescription())
            .referenceNumber(transaction.getReferenceNumber())
            .timestamp(transaction.getTimestamp())
            .runningBalance(transaction.getRunningBalance())
            .build();
    }
}
