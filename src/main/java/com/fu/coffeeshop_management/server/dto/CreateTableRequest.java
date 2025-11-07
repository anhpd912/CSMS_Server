package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableRequest {

    @NotBlank(message = "Table name is required")
    @Size(min = 1, max = 50, message = "Table name must be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Pattern(regexp = "Indoor|Outdoor|Balcony", message = "Location must be Indoor, Outdoor, or Balcony")
    private String location;

    @NotNull(message = "Sheet count is required")
    @Min(value = 1, message = "Sheet count must be at least 1")
    @Max(value = 20, message = "Sheet count cannot exceed 20")
    private Integer sheetCount;

    @Pattern(regexp = "Available|Occupied|Reserved", message = "Status must be Available, Occupied, or Reserved")
    private String status; // Default will be "Available" if not provided
}
