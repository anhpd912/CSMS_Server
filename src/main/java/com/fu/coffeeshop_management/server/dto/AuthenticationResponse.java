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

    /**
     * The JWT (JSON Web Token) for the user.
     */
    private String token;
}
