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
    private final PaymentRepository paymentRepository;
    private final LoyaltyRepository loyaltyRepository;

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
        // 1. Thực hiện tính toán
        CalculationResult result = performCalculation(request);

        // 2. Kiểm tra xem bill đã tồn tại cho order này chưa
        if (billRepository.existsByOrderId(request.getOrderId())) {
            throw new ConflictException("Bill already generated for this order.");
        }

        // 3. (THAY ĐỔI) Không cần tạo Payment nữa.
        // Tạo và lưu Bill (Dùng entity Bill đã sửa)
        Bill bill = Bill.builder()
                .order(result.order)
                .customer(result.customer) // Lưu customer (nếu có)
                .voucher(result.voucher) // Lưu voucher (nếu có)
                .subtotal(result.subtotal) // Lưu subtotal
                .discount(result.totalDiscount) // Lưu tổng giảm giá
                .pointsRedeemed(result.pointsRedeemed) // Lưu điểm đã dùng
                .finalAmount(result.finalTotal)
                .tax(BigDecimal.ZERO) // Giả định tax = 0
                .issuedTime(LocalDateTime.now())
                .paymentStatus("Pending Payment") // Post-Condition POS-01
                .build();

        Bill savedBill = billRepository.save(bill);

        // (POS-02: Điểm sẽ được trừ ở UC-0205, ở đây chỉ ghi tạm vào bill)

        // 5. Map sang DTO Response
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

        if (!"Completed".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Order is not 'Completed' and cannot be billed.");
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

        // 3. Xử lý Khách hàng (Step 4)
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isEmpty()) {
            customer = customerRepository.findByPhone(request.getCustomerPhone()).orElse(null);
        }

        // 4. Xử lý Voucher (Step 5 & AT2) - LOGIC MỚI
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
                voucherDiscount = subtotal.multiply(voucher.getDiscountValue()).divide(ONE_HUNDRED);
            }
        }

        // 5. Xử lý Điểm (Step 6 & AT3)
        if (customer != null && pointsToRedeem > 0) {
            // Cần fetch LAZY
            int availablePoints = customer.getLoyalty().getPoints();
            if (pointsToRedeem > availablePoints) {
                throw new BadRequestException("Not enough points to redeem."); // AT3.1
            }
            pointsDiscount = POINT_CONVERSION_RATE.multiply(new BigDecimal(pointsToRedeem));
        } else {
            pointsToRedeem = 0; // Đảm bảo là 0 nếu không có khách
        }

        // 6. Tính toán tổng (Step 7)
        BigDecimal totalDiscount = voucherDiscount.add(pointsDiscount);
        BigDecimal finalTotal = subtotal.subtract(totalDiscount);

        // Đảm bảo tổng tiền không bị âm
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

        // 1. Tìm Bill (Xử lý AT1 - Bill Not Found)
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Selected bill no longer exists or was deleted."));

        // 2. Tải các đối tượng liên quan (LAZY loading)
        Order order = bill.getOrder();
        User cashier = order.getStaff();
        TableInfo table = order.getTable();
        Customer customer = bill.getCustomer(); // Có thể null
        Voucher voucher = bill.getVoucher(); // Có thể null

        // 3. Lắp ráp thông tin khách hàng (nếu có)
        BillCustomerDTO customerDTO = null;
        if (customer != null) {
            customerDTO = BillCustomerDTO.builder()
                    .customerName(customer.getFullName())
                    .phone(customer.getPhone())
                    .build();
        }

        // 4. Lắp ráp danh sách món hàng
        List<BillItemDTO> itemDTOs = order.getOrderDetails().stream()
                .map(this::mapToBillItemDTO)
                .collect(Collectors.toList());

        // 5. Lắp ráp danh sách thanh toán (nếu có)
        // (Chúng ta dùng repo riêng để tránh N+1 nếu dùng bill.getBillPayments())
        List<BillPayment> payments = billPaymentRepository.findByBillId(billId);
        List<BillPaymentDTO> paymentDTOs = payments.stream()
                .map(this::mapToBillPaymentDTO)
                .collect(Collectors.toList());

        // 6. Xây dựng DTO tổng
        return BillDetailResponse.builder()
                .billId(bill.getId())
                .orderId(order.getId())
                .issuedTime(bill.getIssuedTime())
                .paymentStatus(bill.getPaymentStatus())
                .tableName(table.getName())
                .cashierName(cashier.getFullname())
                .customerInfo(customerDTO) // DTO khách hàng
                .items(itemDTOs)           // List DTO món
                .subtotal(bill.getSubtotal())
                .totalDiscount(bill.getDiscount())
                .finalAmount(bill.getFinalAmount())
                .voucherCode(voucher != null ? voucher.getCode() : null)
                .pointsRedeemed(bill.getPointsRedeemed())
                .payments(paymentDTOs)     // List DTO thanh toán
                .build();
    }

    // --- Các hàm helper private ---

    private BillItemDTO mapToBillItemDTO(OrderDetail detail) {
        BigDecimal price = detail.getPrice();
        int quantity = detail.getQuantity();
        return BillItemDTO.builder()
                .productName(detail.getProduct().getName()) // Kích hoạt LAZY load Product
                .quantity(quantity)
                .priceAtOrder(price)
                .lineTotal(price.multiply(BigDecimal.valueOf(quantity)))
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
        // 1. Xác định khoảng thời gian. Mặc định là ngày hôm nay.
        LocalDate queryDate = (date == null) ? LocalDate.now() : date;
        LocalDateTime startOfDay = queryDate.atStartOfDay();
        LocalDateTime endOfDay = queryDate.atTime(LocalTime.MAX);

        // 2. Query 1: Lấy tất cả Bills (với Order và User)
        List<Bill> bills = billRepository.findBillsByDateRangeFetch(startOfDay, endOfDay);

        // Xử lý AT1: Không tìm thấy hóa đơn
        if (bills.isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng
        }

        // 3. Tối ưu N+1: Lấy tất cả payment liên quan trong 1 query
        List<UUID> billIds = bills.stream().map(Bill::getId).collect(Collectors.toList());
        List<BillPayment> payments = billPaymentRepository.findByBillIdIn(billIds);

        // 4. Nhóm các payment theo billId để tra cứu nhanh
        Map<UUID, List<BillPayment>> paymentsByBillId = payments.stream()
                .collect(Collectors.groupingBy(bp -> bp.getBill().getId()));

        // 5. Chuyển đổi sang DTO
        return bills.stream()
                .map(bill -> mapToBillSummaryDTO(bill, paymentsByBillId.get(bill.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Hàm helper để chuyển đổi Bill -> BillSummaryDTO
     */
    private BillSummaryDTO mapToBillSummaryDTO(Bill bill, List<BillPayment> payments) {

        // Logic nghiệp vụ cho "Payment Method" (Step 3)
        String paymentMethod;
        if (payments == null || payments.isEmpty()) {
            paymentMethod = "Pending"; // Hoặc N/A
        } else if (payments.size() == 1) {
            paymentMethod = payments.get(0).getPaymentMethod(); // "Cash", "QR"
        } else {
            paymentMethod = "Multiple"; // Ví dụ: 1 nửa Cash, 1 nửa QR
        }

        return BillSummaryDTO.builder()
                .billId(bill.getId())
                .issuedTime(bill.getIssuedTime())
                .finalAmount(bill.getFinalAmount())
                .paymentStatus(bill.getPaymentStatus())
                .cashierName(bill.getOrder().getStaff().getFullname()) // Đã được fetch
                .paymentMethod(paymentMethod)
                .build();
    }

    @Transactional
    public PaymentConfirmationResponse confirmPayment(UUID billId, PaymentConfirmationRequest request) {

        // 1. Tải hóa đơn (Step 1) và kiểm tra (PRE-02)
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found."));

        if (!"Pending Payment".equalsIgnoreCase(bill.getPaymentStatus())) {
            throw new ConflictException("This bill is not pending payment. Current status: " + bill.getPaymentStatus());
        }

        // 2. Xử lý Alternative Flow AT2
        if (!"Cash".equalsIgnoreCase(request.getPaymentMethod()) && !"Banking".equalsIgnoreCase(request.getPaymentMethod())) {
            // (AT2.1)
            throw new BadRequestException("This method is temporarily unavailable.");
        }

        // 3. Xử lý thanh toán (Step 5)
        // Tạo một record Payment mới
        Payment newPayment = new Payment();
        newPayment.setBill(bill);
        newPayment.setAmount(bill.getFinalAmount()); // Lấy số tiền cuối cùng từ bill
        newPayment.setMethod(request.getPaymentMethod());
        newPayment.setPaymentTime(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(newPayment);

        // Cập nhật trạng thái Bill
        bill.setPaymentStatus("Paid");
        billRepository.save(bill);

        // 4. Xử lý điểm Loyalty (Step 6)
        int pointsEarned = 0;
        int pointsSpent = 0;

        if (bill.getCustomer() != null) {
            Customer customer = bill.getCustomer();
            Loyalty loyalty = customer.getLoyalty();

            if (loyalty != null) {
                // 4a. Trừ điểm đã tiêu (lưu ở UC-0301)
                pointsSpent = bill.getPointsRedeemed();

                // 4b. Cộng điểm vừa kiếm được
                // (Giả sử tính điểm trên subtotal, trước khi giảm giá)
                pointsEarned = bill.getSubtotal()
                        .divide(POINT_EARNING_RATE, 0, RoundingMode.FLOOR)
                        .intValue();

                // 4c. Cập nhật tổng điểm
                int currentPoints = loyalty.getPoints();
                int newTotalPoints = currentPoints - pointsSpent + pointsEarned;
                loyalty.setPoints(newTotalPoints);
                loyaltyRepository.save(loyalty);
            }
        }

        // 5. Trả về xác nhận (Step 7)
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
