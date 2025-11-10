package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning a successful authentication response.
 * Based on the SDD 'AuthenticationResponse' class.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private Boolean isSuccess;
    private String  message;
    private String  errorCode;
    private String role;
    private String token;
}
