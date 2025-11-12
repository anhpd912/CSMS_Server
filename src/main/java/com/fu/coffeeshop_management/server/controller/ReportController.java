package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.ItemReportDTO;
import com.fu.coffeeshop_management.server.dto.PeriodItemReportDTO;
import com.fu.coffeeshop_management.server.dto.RevenueReportDTO;
import com.fu.coffeeshop_management.server.dto.StockReportDTO;
import com.fu.coffeeshop_management.server.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/revenue") // Endpoint: /api/reports/revenue
    public ResponseEntity<RevenueReportDTO> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "DAY") String filterBy
    ) {
        RevenueReportDTO report = reportService.getRevenueReport(dateFrom, dateTo, filterBy);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/items")
    public ResponseEntity<List<PeriodItemReportDTO>> getItemReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "DAY") String filterBy
    ) {
        List<PeriodItemReportDTO> report = reportService.getItemReport(dateFrom, dateTo, filterBy);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/stock")
    public ResponseEntity<StockReportDTO> getStockReport() {
        StockReportDTO report = reportService.getStockReport();
        return ResponseEntity.ok(report);
    }
}