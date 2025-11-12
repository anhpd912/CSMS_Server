package com.fu.coffeeshop_management.server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@NoArgsConstructor
public class RevenueReportDTO {
    private BigDecimal totalRevenue;

    private Long totalBills;

    private BigDecimal avgRevenuePerBill;

    private List<RevenueByTimeDTO> details;

    public RevenueReportDTO(BigDecimal totalRevenue, Long totalBills, List<RevenueByTimeDTO> details) {
        this.totalRevenue = (totalRevenue == null) ? BigDecimal.ZERO : totalRevenue;
        this.totalBills = (totalBills == null) ? 0L : totalBills;
        this.details = details;

        if (this.totalBills > 0) {
            this.avgRevenuePerBill = this.totalRevenue.divide(
                    BigDecimal.valueOf(this.totalBills),
                    0,
                    RoundingMode.HALF_UP
            );
        } else {
            this.avgRevenuePerBill = BigDecimal.ZERO;
        }
    }
}
