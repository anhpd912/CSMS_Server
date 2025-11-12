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
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest, @AuthenticationPrincipal User currentUser) {
        try {
            orderService.createOrderFromDTO(orderRequest, currentUser.getId());

            Map<String, Object> response = Map.of(
                "isSuccess", true,
                "message", "Create order successfully."
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Undefined error during create order. Type of error: " + e.getClass().getSimpleName();
            } else {
                errorMessage = "Error in creating order: " + errorMessage;
            }

            Map<String, Object> errorResponse = Map.of(
                "isSuccess", false,
                "message", errorMessage
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody OrderRequestDTO orderRequest, @AuthenticationPrincipal User currentUser) {
        try {
            orderService.updateOrder(id, orderRequest, currentUser.getId());

            Map<String, Object> response = Map.of(
                    "isSuccess", true,
                    "message", "Update order successfully."
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Undefined error during update order. Type of error: " + e.getClass().getSimpleName();
            } else {
                errorMessage = "Error in updating order: " + errorMessage;
            }
            Map<String, Object> errorResponse = Map.of(
                    "isSuccess", false,
                    "message", errorMessage
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
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
