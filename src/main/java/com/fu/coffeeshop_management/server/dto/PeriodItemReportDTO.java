package com.fu.coffeeshop_management.server.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodItemReportDTO {

    private String periodLabel;
    private List<ItemReportDetailDTO> topItems;
    private List<ItemReportDetailDTO> bottomItems;
}