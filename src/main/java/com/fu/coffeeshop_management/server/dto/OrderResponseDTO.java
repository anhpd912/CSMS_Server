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
    private List<String> tableNames;
    private List<TableInfoDTO> tables;
    private List<OrderItemResponseDTO> items;
}
