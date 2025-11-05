package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for returning ingredient (Product) information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageLink;
    private String status;
    private String categoryName;
    
    // Stock information
    private Integer quantityInStock;
    private Integer reorderLevel;
    private Boolean isLowStock;
}
