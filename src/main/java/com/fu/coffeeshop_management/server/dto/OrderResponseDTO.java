package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;
    private Instant orderDate;
    private double totalAmount;
    private String status;
    private String staffName;
    private String note;
    private List<String> tableNames; // Giữ lại để tương thích
    private List<TableInfoDTO> tables; // Trường mới chứa cả id và name
    private List<OrderItemResponseDTO> items;
}
