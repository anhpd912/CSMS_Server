package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.OrderRequestDTO;
import com.fu.coffeeshop_management.server.dto.OrderResponseDTO;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders") // Sửa thành /api/v1/orders cho nhất quán
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest, @AuthenticationPrincipal User currentUser) {
        try {
            // 1. Gọi service, truyền userId thay vì toàn bộ đối tượng User
            orderService.createOrderFromDTO(orderRequest, currentUser.getId());

            // 2. Nếu không có lỗi, tạo và trả về một thông báo thành công đơn giản
            Map<String, Object> response = Map.of(
                "isSuccess", true,
                "message", "Tạo đơn hàng thành công"
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 3. Nếu có lỗi, trả về thông báo lỗi
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Đã xảy ra lỗi không xác định trong quá trình tạo đơn hàng. Loại lỗi: " + e.getClass().getSimpleName();
            } else {
                errorMessage = "Lỗi tạo đơn hàng: " + errorMessage;
            }

            Map<String, Object> errorResponse = Map.of(
                "isSuccess", false,
                "message", errorMessage
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    // Sửa Long id thành String id
    public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody OrderRequestDTO orderRequest, @AuthenticationPrincipal User currentUser) {
        try {
            OrderResponseDTO updatedOrder = orderService.updateOrder(id, orderRequest, currentUser.getId());
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Đã xảy ra lỗi không xác định trong quá trình cập nhật đơn hàng. Loại lỗi: " + e.getClass().getSimpleName();
            } else {
                errorMessage = "Lỗi cập nhật đơn hàng: " + errorMessage;
            }
            Map<String, Object> errorResponse = Map.of(
                "isSuccess", false,
                "message", errorMessage
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    // Thêm chức năng lấy chi tiết đơn hàng
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }


    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String tableId,
            @RequestParam(required = false) String staffId) {
        List<OrderResponseDTO> orders = orderService.getOrders(fromDate, toDate, status, tableId, staffId);
        return ResponseEntity.ok(orders);
    }
}
