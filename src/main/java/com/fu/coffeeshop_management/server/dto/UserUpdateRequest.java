package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private UUID userId;
    private String email;
    private String password;
    private String fullname;
    private String mobile;
    private UUID roleId;
}
