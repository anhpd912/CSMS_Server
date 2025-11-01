package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for returning public user information.
 * This is used for the "me" endpoint.
 * Based on the SDD 'UserResponse' class.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String fullname;
    private String mobile;
    private String role;
}
