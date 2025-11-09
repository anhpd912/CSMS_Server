package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDTO {
    private UUID id;
    private String name;
    private String location;
    private String status;
    private Integer seat_count;
    // Collections like 'reservations' and 'orders' are omitted to prevent serialization issues
}
