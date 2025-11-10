package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTableRequest {

    @NotBlank(message = "Table name is required")
    @Size(min = 1, max = 50, message = "Table name must be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Pattern(regexp = "Indoor|Outdoor|Balcony", message = "Location must be Indoor, Outdoor, or Balcony")
    private String location;

    @NotNull(message = "Seat count is required")
    @Min(value = 1, message = "Seat count must be at least 1")
    @Max(value = 20, message = "Seat count cannot exceed 20")
    private Integer seatCount;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Available|Occupied|Reserved", message = "Status must be Available, Occupied, or Reserved")
    private String status;
}
