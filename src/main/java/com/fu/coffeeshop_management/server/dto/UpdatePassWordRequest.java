package com.fu.coffeeshop_management.server.dto;


import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UpdatePassWordRequest {
    private String email;
    private String oldPassword;
    @Length(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
