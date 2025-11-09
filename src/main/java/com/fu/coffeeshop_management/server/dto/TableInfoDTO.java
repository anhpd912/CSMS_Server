package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDTO {
    private UUID id;
    private String name;
    private String status;
    private String location;
    private int seat_count;
    //private List<SimpleOrderDTO> orders;

    // Constructor mới được thêm vào
    public TableInfoDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
