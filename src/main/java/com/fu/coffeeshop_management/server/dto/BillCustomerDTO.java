package com.fu.coffeeshop_management.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillCustomerDTO {
    private String phone;
    private String name;
}
