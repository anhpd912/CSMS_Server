package com.fu.coffeeshop_management.server.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReportDTO {
    private Long totalItems;
    private Long lowStockItems;

    private List<StockItemDetailDTO> details;
}