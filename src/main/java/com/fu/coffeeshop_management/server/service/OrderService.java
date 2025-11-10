package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.dto.OrderRequestDTO;
import com.fu.coffeeshop_management.server.dto.OrderItemRequestDTO;
import com.fu.coffeeshop_management.server.entity.*;
import com.fu.coffeeshop_management.server.repository.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private TableInfoRepository tableInfoRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository; // Inject UserRepository

    @Transactional
    public void createOrderFromDTO(OrderRequestDTO request, UUID userId) { // Thay đổi tham số thành String userId

        // Tải lại đối tượng User từ database để đảm bảo nó được quản lý bởi phiên hiện tại
        User staff = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // 1. Tạo đối tượng Order ngay từ đầu
        Order newOrder = new Order();
        newOrder.setCreatedAt(LocalDateTime.now());
        log.info("Order created at: {}", newOrder.getCreatedAt());
        newOrder.setStatus("SERVING");

        newOrder.setStaff(staff); // Sử dụng đối tượng User đã tải lại
        log.info("id: {}", staff.getId());

        newOrder.setOrderDetails(new HashSet<>());
        newOrder.setTableOrders(new HashSet<>());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. Xử lý các món trong đơn hàng (Order Details)
        for (OrderItemRequestDTO itemDto : request.getItems()) {
            Product product = productRepository.findById(UUID.fromString(itemDto.getProductId()))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + itemDto.getProductId()));

            BigDecimal itemQuantity = new BigDecimal(itemDto.getQuantity());
            BigDecimal itemTotal = product.getPrice().multiply(itemQuantity);
            totalAmount = totalAmount.add(itemTotal);

            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(itemDto.getQuantity());
            detail.setPrice(product.getPrice());
            detail.setOrder(newOrder);


            newOrder.getOrderDetails().add(detail);

            log.info("total: {}", totalAmount);
        }

        newOrder.setTotalPrice(totalAmount.doubleValue()); // Gán tổng tiền đã tính

        // 3. Xử lý các bàn được chọn (Table Orders)
        for (String tableId : request.getTableIds()) {
            TableInfo table = tableInfoRepository.findById(UUID.fromString(tableId))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn: " + tableId));

            table.setStatus("SERVING"); // Cập nhật trạng thái bàn

            // --- SỬA 2: THÊM DÒNG NÀY ---
            // Lưu Bàn (TableInfo) ngay lập tức để nó không còn "bẩn"
            // Điều này ngăn xung đột (conflict) với việc lưu Order ở cuối
            tableInfoRepository.save(table);
            // ---------------------------------

            TableOrder tableOrderLink = new TableOrder();
            tableOrderLink.setOrder(newOrder);
            tableOrderLink.setTableInfo(table);

            newOrder.getTableOrders().add(tableOrderLink); // Thêm vào danh sách của Order
        }

        orderRepository.save(newOrder);
    }

    public List<OrderResponseDTO> getOrders(Instant fromDate, Instant toDate, String status, String tableId, String staffId) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.ofInstant(fromDate, ZoneOffset.UTC)));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.ofInstant(toDate, ZoneOffset.UTC)));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (staffId != null && !staffId.isBlank()) {
                predicates.add(cb.equal(root.get("staff").get("id"), UUID.fromString(staffId)));
            }
            if (tableId != null && !tableId.isBlank()) {
                // Join Order -> TableOrder -> TableInfo để lọc theo tableId
                predicates.add(cb.equal(root.join("tableOrders").get("tableInfo").get("id"), UUID.fromString(tableId)));
                query.distinct(true); // Đảm bảo không có đơn hàng trùng lặp nếu một đơn hàng có nhiều bàn
            }

            // Sắp xếp mặc định: đơn hàng mới nhất lên đầu
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Order> orders = orderRepository.findAll(spec);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO convertToDto(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .orderDate(order.getCreatedAt().toInstant(ZoneOffset.UTC))
                .totalAmount(order.getTotalPrice())
                .status(order.getStatus())
                .staffName(order.getStaff() != null ? order.getStaff().getUsername() : "N/A")
                .tableNames(order.getTableOrders().stream()
                        .map(tableOrder -> tableOrder.getTableInfo().getName())
                        .collect(Collectors.toList()))
                .items(order.getOrderDetails().stream()
                        .map(this::convertDetailToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponseDTO convertDetailToDto(OrderDetail detail) {
        return OrderItemResponseDTO.builder()
                .productName(detail.getProduct().getName())
                .quantity(detail.getQuantity())
                .price(detail.getPrice().doubleValue())
                .build();
    }
}
