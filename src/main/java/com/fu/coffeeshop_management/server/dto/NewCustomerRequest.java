package com.fu.coffeeshop_management.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewCustomerRequest {
    private String phone;
    private String name;
}
