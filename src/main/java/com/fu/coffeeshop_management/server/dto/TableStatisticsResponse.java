package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for table statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableStatisticsResponse {

    private Integer totalTables;
    
    private Integer availableTables;
    
    private Integer occupiedTables;
    
    private Integer reservedTables;
    
    // Statistics by location
    private Map<String, Integer> tablesByLocation;
    
    // Statistics by seat capacity
    private Map<String, Integer> tablesByCapacity;
    
    // Occupancy rate (percentage)
    private Double occupancyRate;
    
    // Average seats per table
    private Double averageSeatsPerTable;
}
