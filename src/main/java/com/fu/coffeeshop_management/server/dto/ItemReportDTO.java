package com.fu.coffeeshop_management.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemReportDTO {
    private List<ItemReportDetailDTO> topItems;
    private List<ItemReportDetailDTO> bottomItems;
}