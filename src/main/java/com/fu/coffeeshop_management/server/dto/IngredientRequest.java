package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating or updating an ingredient (Product with category "Ingredient")
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequest {

    @NotBlank(message = "Ingredient name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 digits and 2 decimal places")
    private BigDecimal price;

    @Min(value = 0, message = "Reorder level must be 0 or greater")
    private Integer reorderLevel;

    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;

    @Size(max = 255, message = "Image link must not exceed 255 characters")
    private String imageLink;

    @Pattern(regexp = "active|inactive", message = "Status must be either 'active' or 'inactive'")
    private String status;
}
