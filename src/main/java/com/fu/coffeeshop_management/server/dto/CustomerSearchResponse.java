package com.fu.coffeeshop_management.server.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class CustomerSearchResponse {
    private UUID customerId;
    private String name;
    private String phone;
    private int points;
}
