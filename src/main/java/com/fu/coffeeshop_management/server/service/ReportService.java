package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.repository.BillRepository;
import com.fu.coffeeshop_management.server.repository.OrderDetailRepository;
import com.fu.coffeeshop_management.server.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    public RevenueReportDTO getRevenueReport(LocalDateTime startDate, LocalDateTime endDate, String filterBy) {

        List<Object[]> rawData;
        String filter = (filterBy == null) ? "DAY" : filterBy.toUpperCase();

        switch (filter) {
            case "MONTH":
                rawData = billRepository.getRevenueReportByMonthFromBills(startDate, endDate);
                break;
            case "WEEK":
                rawData = billRepository.getRevenueReportByWeekFromBills(startDate, endDate);
                break;
            case "DAY":
            default:
                rawData = billRepository.getRevenueReportByDayFromBills(startDate, endDate);
                break;
        }

        if (rawData == null) {
            rawData = Collections.emptyList();
        }

        List<RevenueByTimeDTO> details = rawData.stream()
                .map(row -> {
                    String timeLabel = "";
                    BigDecimal revenue = BigDecimal.ZERO;
                    Long bills = 0L;

                    if ("MONTH".equals(filter)) {
                        timeLabel = row[0] + "-" + String.format("%02d", row[1]);
                        revenue = (row[2] instanceof Number) ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
                        bills = (row[3] instanceof Number) ? ((Number) row[3]).longValue() : 0L;
                    } else if ("WEEK".equals(filter)) {
                        timeLabel = row[0].toString();
                        revenue = (row[1] instanceof Number) ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
                        bills = (row[2] instanceof Number) ? ((Number) row[2]).longValue() : 0L;
                    } else {
                        timeLabel = row[0].toString();
                        revenue = (row[1] instanceof Number) ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
                        bills = (row[2] instanceof Number) ? ((Number) row[2]).longValue() : 0L;
                    }
                    return new RevenueByTimeDTO(timeLabel, revenue, bills);
                })
                .collect(Collectors.toList());

        BigDecimal totalRevenue = details.stream()
                .map(RevenueByTimeDTO::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalBills = details.stream()
                .mapToLong(RevenueByTimeDTO::getBillCount)
                .sum();

        return new RevenueReportDTO(totalRevenue, totalBills, details);
    }

    public List<PeriodItemReportDTO> getItemReport(LocalDateTime dateFrom, LocalDateTime dateTo, String filterBy) {

        List<PeriodItemReportDTO> fullReport = new ArrayList<>();

        LocalDate start = dateFrom.toLocalDate();
        LocalDate end = dateTo.toLocalDate();

        if ("DAY".equalsIgnoreCase(filterBy)) {
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

                LocalDateTime periodStart = date.atStartOfDay();
                LocalDateTime periodEnd = date.atTime(LocalTime.MAX);

                PeriodItemReportDTO dailyReport = generateReportForPeriod(periodStart, periodEnd);
                dailyReport.setPeriodLabel(date.format(DateTimeFormatter.ISO_LOCAL_DATE));

                fullReport.add(dailyReport);
            }
        }
        else if ("WEEK".equalsIgnoreCase(filterBy)) {
            LocalDate periodStart = start;
            while (!periodStart.isAfter(end)) {

                LocalDate periodEnd = periodStart.plusDays(6);
                if (periodEnd.isAfter(end)) {
                    periodEnd = end;
                }

                PeriodItemReportDTO weeklyReport = generateReportForPeriod(
                        periodStart.atStartOfDay(),
                        periodEnd.atTime(LocalTime.MAX)
                );
                int year = periodStart.getYear();
                int weekNumber = periodStart.get(WeekFields.ISO.weekOfWeekBasedYear());
                String weekLabel = String.format("%d%02d", year, weekNumber);

                weeklyReport.setPeriodLabel(weekLabel);

                fullReport.add(weeklyReport);
                periodStart = periodEnd.plusDays(1);
            }
        }
        else if ("MONTH".equalsIgnoreCase(filterBy)) {
            LocalDate currentMonth = start.withDayOfMonth(1);

            while (!currentMonth.isAfter(end)) {
                LocalDate periodStart = (currentMonth.isBefore(start.withDayOfMonth(1))) ? start : currentMonth;

                LocalDate periodEnd = currentMonth.with(TemporalAdjusters.lastDayOfMonth());
                if (periodEnd.isAfter(end)) {
                    periodEnd = end;
                }

                PeriodItemReportDTO monthlyReport = generateReportForPeriod(
                        periodStart.atStartOfDay(),
                        periodEnd.atTime(LocalTime.MAX)
                );
                monthlyReport.setPeriodLabel(periodStart.format(DateTimeFormatter.ofPattern("MM-yyyy")));
                fullReport.add(monthlyReport);

                currentMonth = currentMonth.plusMonths(1);
            }
        }

        return fullReport;
    }

    private PeriodItemReportDTO generateReportForPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {

        List<Object[]> topData = orderDetailRepository.findTopSellingItems(periodStart, periodEnd);

        List<Object[]> bottomData = orderDetailRepository.findBottomSellingItems(periodStart, periodEnd);

        List<ItemReportDetailDTO> topItems = convertToItemDTO(topData);
        List<ItemReportDetailDTO> bottomItems = convertToItemDTO(bottomData);

        return new PeriodItemReportDTO(null, topItems, bottomItems);
    }

    private List<ItemReportDetailDTO> convertToItemDTO(List<Object[]> rawData) {
        return rawData.stream()
                .map(row -> new ItemReportDetailDTO(
                        (String) row[0],
                        (Long) row[1],
                        (BigDecimal) row[2]
                ))
                .collect(Collectors.toList());
    }

    public StockReportDTO getStockReport() {
        List<StockItemDetailDTO> details = productRepository.getIngredientStockDetails();
        Long totalItems = productRepository.countTotalIngredients();
        Long lowStockItems = productRepository.countLowStockIngredients();
        return new StockReportDTO(totalItems, lowStockItems, details);
    }
}
