package com.fu.coffeeshop_management.server.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UpdatePassWordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
