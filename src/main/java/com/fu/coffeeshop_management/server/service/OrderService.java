package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private TableInfoRepository tableInfoRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private TableOrderRepository tableOrderRepository;

    @Transactional
    public void createOrderFromDTO(OrderRequestDTO request, UUID userId) {

        User staff = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        Order newOrder = new Order();
        newOrder.setStatus("SERVING");
        newOrder.setNote(request.getNote());
        newOrder.setStaff(staff);
        newOrder.setOrderDetails(new HashSet<>());
        newOrder.setTableOrders(new HashSet<>());

        BigDecimal totalAmount = BigDecimal.ZERO;

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
        }

        newOrder.setTotalPrice(totalAmount.doubleValue());

        for (String tableId : request.getTableIds()) {
            TableInfo table = tableInfoRepository.findById(UUID.fromString(tableId))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn: " + tableId));

            table.setStatus("SERVING");
            tableInfoRepository.save(table);

            TableOrder tableOrderLink = new TableOrder();
            tableOrderLink.setOrder(newOrder);
            tableOrderLink.setTableInfo(table);

            newOrder.getTableOrders().add(tableOrderLink);
        }

        orderRepository.save(newOrder);
    }

    @Transactional
    public OrderResponseDTO updateOrder(String orderId, OrderRequestDTO request, UUID userId) {
        UUID orderUUID = UUID.fromString(orderId);

        // 1. Tìm đơn hàng hiện có
        Order orderToUpdate = orderRepository.findById(orderUUID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        // 2. Cập nhật các trường đơn giản
        orderToUpdate.setNote(request.getNote());

        // Cập nhật status nếu có trong request
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            orderToUpdate.setStatus(request.getStatus());
        }

        User staff = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        orderToUpdate.setStaff(staff);

        // 3. Xử lý OrderDetails - THE CORRECT WAY
        updateOrderDetails(orderToUpdate, request.getItems());

        // 5. Lưu đơn hàng đã cập nhật (JPA sẽ tự động xử lý các collection đã được thay thế)
        Order savedOrder = orderRepository.save(orderToUpdate);

        // 6. Trả về DTO
        return convertToDto(savedOrder);
    }

    private void updateOrderDetails(Order order, List<OrderItemRequestDTO> itemRequests) {
        // 1. Xóa các chi tiết cũ một cách rõ ràng
        orderDetailRepository.deleteAllInBatch(order.getOrderDetails());

        // 2. Tạo một collection HOÀN TOÀN MỚI
        Set<OrderDetail> newDetails = new HashSet<>();
        BigDecimal newTotalAmount = BigDecimal.ZERO;

        // 3. Đổ dữ liệu vào collection MỚI
        for (OrderItemRequestDTO itemDto : itemRequests) {
            Product product = productRepository.findById(UUID.fromString(itemDto.getProductId()))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + itemDto.getProductId()));

            OrderDetail newDetail = new OrderDetail();
            newDetail.setProduct(product);
            newDetail.setQuantity(itemDto.getQuantity());
            newDetail.setPrice(product.getPrice());
            newDetail.setOrder(order); // Liên kết ngược lại

            newDetails.add(newDetail); // Thêm vào collection MỚI

            // Tính toán tổng tiền
            BigDecimal itemQuantity = new BigDecimal(itemDto.getQuantity());
            BigDecimal itemTotal = product.getPrice().multiply(itemQuantity);
            newTotalAmount = newTotalAmount.add(itemTotal);
        }

        // 4. THAY THẾ collection cũ bằng collection mới và cập nhật tổng tiền
        order.setOrderDetails(newDetails);
        order.setTotalPrice(newTotalAmount.doubleValue());
    }

    public OrderResponseDTO getOrderById(String orderId) {
        UUID orderUUID = UUID.fromString(orderId);
        Order order = orderRepository.findById(orderUUID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        return convertToDto(order);
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
                predicates.add(cb.equal(root.join("tableOrders").get("tableInfo").get("id"), UUID.fromString(tableId)));
                query.distinct(true);
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Order> orders = orderRepository.findAll(spec);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO convertToDto(Order order) {
        // Tạo danh sách DTO cho bàn (chứa id và name)
        List<TableInfoDTO> tableInfoDTOs = order.getTableOrders().stream()
                .map(tableOrder -> {
                    TableInfo tableInfo = tableOrder.getTableInfo();
                    return new TableInfoDTO(tableInfo.getId(), tableInfo.getName());
                })
                .collect(Collectors.toList());

        // Trích xuất danh sách tên bàn cho trường cũ
        List<String> tableNames = tableInfoDTOs.stream()
                .map(TableInfoDTO::getName)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId().toString())
                .orderDate(order.getCreatedAt().toInstant(ZoneOffset.UTC))
                .totalAmount(order.getTotalPrice())
                .status(order.getStatus())
                .staffName(order.getStaff() != null ? order.getStaff().getUsername() : "N/A")
                .note(order.getNote())
                .tableNames(tableNames) // Trường cũ để tương thích
                .tables(tableInfoDTOs) // Trường mới với đầy đủ thông tin
                .items(order.getOrderDetails().stream()
                        .map(this::convertDetailToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponseDTO convertDetailToDto(OrderDetail detail) {
        Product product = detail.getProduct();
        return OrderItemResponseDTO.builder()
                .productId(product.getId().toString())
                .productName(product.getName())
                .quantity(detail.getQuantity())
                .price(detail.getPrice().doubleValue())
                .build();
    }
}
