package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for table information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {

    private UUID id;
    
    private String name;
    
    private String location;
    
    private Integer sheetCount;
    
    private String status;
    
    // Additional computed fields
    private Boolean isAvailable;
    
    private Integer currentOrders; // Number of active orders on this table
}
