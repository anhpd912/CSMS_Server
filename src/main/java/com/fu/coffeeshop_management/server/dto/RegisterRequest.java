package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling user registration requests.
 * Your SDD mentions "Create account" (UC-0103), this DTO supports that.
 */ 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotEmpty(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullname;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    // In a real app, you'd add more complexity validation (uppercase, number, etc.)
    private String password;

    @NotEmpty(message = "Mobile number is required")
    @Size(min = 10, max = 20, message = "Mobile number must be between 10 and 20 digits")
    private String mobile;
    
    // Note: Role is not included here. We will assign a default role
    // or have a separate admin endpoint to create users with specific roles.
    // For this base, we'll default to "WAITER".
}
