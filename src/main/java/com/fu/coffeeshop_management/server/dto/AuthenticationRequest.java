package com.fu.coffeeshop_management.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling authentication (login) requests.
 * Based on the SDD 'AuthenticationRequest' class.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;
}
