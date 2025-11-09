package com.fu.coffeeshop_management.server.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestDTO {

    private List<String> tableIds;
    private List<OrderItemRequestDTO> items;


    public OrderRequestDTO() {
        this.tableIds = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public OrderRequestDTO(List<String> tableIds, List<OrderItemRequestDTO> items) {
        this.tableIds = tableIds;
        this.items = items;
    }

    public List<String> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<String> tableIds) {
        this.tableIds = tableIds;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }
}