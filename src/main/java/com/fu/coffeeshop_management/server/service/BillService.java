package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.*;
import com.fu.coffeeshop_management.server.exception.BadRequestException;
import com.fu.coffeeshop_management.server.exception.ConflictException;
import com.fu.coffeeshop_management.server.exception.NotFoundException;
import com.fu.coffeeshop_management.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final LoyaltyRepository loyaltyRepository;
    private final LoyaltyService loyaltyService;

    private static final BigDecimal POINT_CONVERSION_RATE = new BigDecimal("1000");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal POINT_EARNING_RATE = new BigDecimal("10000");

    @Transactional(readOnly = true)
    public BillCalculationResponse calculateBill(BillGenerationRequest request) {
        CalculationResult result = performCalculation(request);

        BillCalculationResponse response = new BillCalculationResponse();
        response.setSubtotal(result.subtotal);
        response.setDiscount(result.totalDiscount);
        response.setFinalTotal(result.finalTotal);

        if (result.customer != null) {
            response.setCustomerName(result.customer.getFullName());
            response.setCustomerAvailablePoints(result.customer.getLoyalty().getPoints());
        }
        return response;
    }

    @Transactional
    public BillResponse generateBill(BillGenerationRequest request) {
        CalculationResult result = performCalculation(request);
        if (billRepository.existsByOrderId(request.getOrderId())) {
            throw new ConflictException("Bill already generated for this order.");
        }

        Bill bill = Bill.builder()
                .order(result.order)
                .customer(result.customer)
                .voucher(result.voucher)
                .subtotal(result.subtotal)
                .discount(result.totalDiscount)
                .pointsRedeemed(result.pointsRedeemed)
                .finalAmount(result.finalTotal)
                .tax(BigDecimal.ZERO)
                .issuedTime(LocalDateTime.now())
                .paymentStatus("Pending Payment")
                .build();

        Bill savedBill = billRepository.save(bill);

        Order order = result.order;
        order.setStatus("COMPLETED");

        if (order.getTableOrders() != null) {
            for (TableOrder tableOrder : order.getTableOrders()) {
                tableOrder.getTableInfo().setStatus("AVAILABLE");
            }
        }
        orderRepository.save(order);

        BillResponse response = new BillResponse();
        response.setBillId(savedBill.getId());
        response.setOrderId(savedBill.getOrder().getId());
        response.setCustomerName(savedBill.getCustomer() != null ? savedBill.getCustomer().getFullName() : null);
        response.setSubtotal(savedBill.getSubtotal());
        response.setDiscountAmount(savedBill.getDiscount());
        response.setFinalTotal(savedBill.getFinalAmount());
        response.setPaymentStatus(savedBill.getPaymentStatus());

        return response;
    }

    private CalculationResult performCalculation(BillGenerationRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + request.getOrderId()));

        if (!"serving".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Order is not in 'SERVING' state and cannot be billed.");
        }

        Set<OrderDetail> orderDetailsSet = order.getOrderDetails();
        List<OrderDetail> safeDetailsList = new ArrayList<>(orderDetailsSet);

        BigDecimal subtotal = safeDetailsList.stream()
                .map(detail -> detail.getPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal voucherDiscount = BigDecimal.ZERO;
        BigDecimal pointsDiscount = BigDecimal.ZERO;
        int pointsToRedeem = request.getPointsToRedeem();

        Customer customer = null;
        Voucher voucher = null;

        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isEmpty()) {
            customer = customerRepository.findByPhone(request.getCustomerPhone()).orElse(null);
        }

        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            voucher = voucherRepository.findByCode(request.getVoucherCode())
                    .orElseThrow(() -> new BadRequestException("Invalid or expired voucher code."));

            LocalDate today = LocalDate.now();
            if (voucher.getStatus() != Voucher.VoucherStatus.ACTIVE) {
                throw new BadRequestException("Voucher is not active.");
            }
            if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(today)) {
                throw new BadRequestException("Voucher is not yet valid.");
            }
            if (voucher.getEndDate() != null && voucher.getEndDate().isBefore(today)) {
                throw new BadRequestException("Voucher expired."); // AT2.1
            }

            if (voucher.getDiscountType() == Voucher.VoucherType.FIXED_AMOUNT) {
                voucherDiscount = voucher.getDiscountValue();
            } else if (voucher.getDiscountType() == Voucher.VoucherType.PERCENT) {
                voucherDiscount = subtotal
                        .multiply(voucher.getDiscountValue())
                        .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);            }
        }

        if (customer != null && pointsToRedeem > 0) {
            int availablePoints = customer.getLoyalty().getPoints();
            if (pointsToRedeem > availablePoints) {
                throw new BadRequestException("Not enough points to redeem.");
            }
            pointsDiscount = POINT_CONVERSION_RATE.multiply(new BigDecimal(pointsToRedeem));
        } else {
            pointsToRedeem = 0;
        }

        BigDecimal totalDiscount = voucherDiscount.add(pointsDiscount);
        BigDecimal finalTotal = subtotal.subtract(totalDiscount);

        finalTotal = finalTotal.max(BigDecimal.ZERO);

        return new CalculationResult(order, customer, voucher, subtotal, totalDiscount, finalTotal, pointsToRedeem);
    }

    private static class CalculationResult {
        final Order order;
        final Customer customer;
        final Voucher voucher;
        final BigDecimal subtotal;
        final BigDecimal totalDiscount;
        final BigDecimal finalTotal;
        final int pointsRedeemed;

        CalculationResult(Order o, Customer c, Voucher v, BigDecimal sub, BigDecimal dis, BigDecimal total, int points) {
            this.order = o;
            this.customer = c;
            this.voucher = v;
            this.subtotal = sub;
            this.totalDiscount = dis;
            this.finalTotal = total;
            this.pointsRedeemed = points;
        }
    }

    @Transactional(readOnly = true)
    public BillDetailResponse getBillDetails(UUID billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Selected bill no longer exists or was deleted."));

        Order order = bill.getOrder();
        User cashier = order.getStaff();
        Customer customer = bill.getCustomer();
        Voucher voucher = bill.getVoucher();

        String tableName = order.getTableOrders().stream()
                .findFirst()
                .map(TableOrder::getTableInfo)
                .map(TableInfo::getName)
                .orElse("N/A");

        BillCustomerDTO customerDTO = null;
        if (customer != null) {
            customerDTO = BillCustomerDTO.builder()
                    .customerName(customer.getFullName())
                    .phone(customer.getPhone())
                    .build();
        }

        List<BillItemDTO> itemDTOs = order.getOrderDetails().stream()
                .map(this::mapToBillItemDTO)
                .collect(Collectors.toList());

        List<BillPayment> payments = billPaymentRepository.findByBillId(billId);
        List<BillPaymentDTO> paymentDTOs = payments.stream()
                .map(this::mapToBillPaymentDTO)
                .collect(Collectors.toList());

        return BillDetailResponse.builder()
                .billId(bill.getId())
                .orderId(order.getId())
                .issuedTime(bill.getIssuedTime())
                .paymentStatus(bill.getPaymentStatus())
                .tableName(tableName)
                .cashierName(cashier.getFullname())
                .customerInfo(customerDTO)
                .items(itemDTOs)
                .subtotal(bill.getSubtotal())
                .totalDiscount(bill.getDiscount())
                .finalAmount(bill.getFinalAmount())
                .voucherCode(voucher != null ? voucher.getCode() : null)
                .pointsRedeemed(bill.getPointsRedeemed())
                .payments(paymentDTOs)
                .build();
    }


    private BillItemDTO mapToBillItemDTO(OrderDetail detail) {
        if (detail == null) return null;

        BigDecimal price = Optional.ofNullable(detail.getPrice()).orElse(BigDecimal.ZERO);
        int quantity = detail.getQuantity();

        String productName = null;
        if (detail.getProduct() != null) {
            try {
                productName = detail.getProduct().getName();
            } catch (org.hibernate.LazyInitializationException lie) {
                productName = "Unknown";
            }
        } else {
            productName = "Unknown";
        }

        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);

        return BillItemDTO.builder()
                .productName(productName)
                .quantity(quantity)
                .priceAtOrder(price.setScale(2, RoundingMode.HALF_UP))
                .lineTotal(lineTotal)
                .build();
    }


    private BillPaymentDTO mapToBillPaymentDTO(BillPayment payment) {
        return BillPaymentDTO.builder()
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<BillSummaryDTO> getBillList(LocalDate date) {
        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;
        List<Bill> bills;

        if (date == null) {
            bills = billRepository.findAllFetch();
        } else {
            LocalDate queryDate = date;
            startOfDay = queryDate.atStartOfDay();
            endOfDay = queryDate.atTime(LocalTime.MAX);
            bills = billRepository.findBillsByDateRangeFetch(startOfDay, endOfDay);
        }

        if (bills == null || bills.isEmpty()) {
            return Collections.emptyList();
        }

        bills.sort(Comparator.comparing(
                        Bill::getIssuedTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());

        List<UUID> billIds = bills.stream().map(Bill::getId).collect(Collectors.toList());
        List<BillPayment> payments = billPaymentRepository.findByBill_IdIn(billIds);

        Map<UUID, List<BillPayment>> paymentsByBillId = payments == null
                ? Collections.emptyMap()
                : payments.stream().collect(Collectors.groupingBy(bp -> bp.getBill().getId()));

        return bills.stream()
                .map(bill -> mapToBillSummaryDTO(bill, paymentsByBillId.get(bill.getId())))
                .collect(Collectors.toList());
    }



    private BillSummaryDTO mapToBillSummaryDTO(Bill bill, List<BillPayment> payments) {

        String paymentMethod;
        if (payments == null || payments.isEmpty()) {
            paymentMethod = "Pending";
        } else if (payments.size() == 1) {
            paymentMethod = payments.get(0).getPaymentMethod();
        } else {
            paymentMethod = "Multiple";
        }

        return BillSummaryDTO.builder()
                .billId(bill.getId())
                .issuedTime(bill.getIssuedTime())
                .finalAmount(bill.getFinalAmount())
                .paymentStatus(bill.getPaymentStatus())
                .cashierName(bill.getOrder().getStaff().getFullname())
                .paymentMethod(paymentMethod)
                .build();
    }

    @Transactional
    public PaymentConfirmationResponse confirmPayment(UUID billId, PaymentConfirmationRequest request) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found."));

        if (!"Pending Payment".equalsIgnoreCase(bill.getPaymentStatus())) {
            throw new ConflictException("This bill is not pending payment. Current status: " + bill.getPaymentStatus());
        }

        if (!"Cash".equalsIgnoreCase(request.getPaymentMethod()) && !"Banking".equalsIgnoreCase(request.getPaymentMethod())) {
            throw new BadRequestException("This method is temporarily unavailable.");
        }

        BillPayment newBillPayment = BillPayment.builder()
                .bill(bill)
                .paymentMethod(request.getPaymentMethod())
                .amount(bill.getFinalAmount())
                .paidAt(LocalDateTime.now())
                .build();

        BillPayment savedPayment = billPaymentRepository.save(newBillPayment);

        bill.setPaymentStatus("Paid");
        billRepository.save(bill);

        int pointsEarned = 0;
        int pointsSpent = 0;

        if (bill.getCustomer() != null) {
            Customer customer = bill.getCustomer();
            Loyalty loyalty = customer.getLoyalty();

            if (loyalty != null) {
                pointsSpent = bill.getPointsRedeemed();

                pointsEarned = bill.getSubtotal()
                        .divide(POINT_EARNING_RATE, 0, RoundingMode.FLOOR)
                        .intValue();

                int currentPoints = loyalty.getPoints();
                int newTotalPoints = currentPoints - pointsSpent + pointsEarned;
                loyalty.setPoints(newTotalPoints);
                loyaltyRepository.save(loyalty);

                loyaltyService.createLoyaltyTransaction(loyalty, bill.getOrder(), pointsEarned, pointsSpent);
            }
        }

        return PaymentConfirmationResponse.builder()
                .billId(bill.getId())
                .paymentId(savedPayment.getId())
                .newStatus("Paid")
                .message("Payment successful.")
                .pointsEarned(pointsEarned)
                .pointsSpent(pointsSpent)
                .build();
    }

}
