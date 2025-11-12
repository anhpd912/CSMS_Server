package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    boolean existsByOrderId(UUID orderId);
    Optional<Bill> findByOrderId(UUID orderId);

    @Query("SELECT b FROM Bill b " +
            "JOIN FETCH b.order o " +
            "JOIN FETCH o.staff " +
            "WHERE b.issuedTime BETWEEN :start AND :end " +
            "ORDER BY b.issuedTime DESC")
    List<Bill> findBillsByDateRangeFetch(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value = "SELECT " +
            "CAST(b.issued_time AS DATE) AS reportDate, " +
            "SUM(b.final_amount) AS totalRevenue, " +
            "COUNT(b.id) AS billCount " +
            "FROM bill b " +
            "WHERE b.payment_status = 'PAID' " +
            "AND b.issued_time BETWEEN :startDate AND :endDate " +
            "GROUP BY reportDate " +
            "ORDER BY reportDate ASC", nativeQuery = true)
    List<Object[]> getRevenueReportByDayFromBills(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = "SELECT " +
            "YEAR(b.issued_time) as reportYear, " +
            "MONTH(b.issued_time) as reportMonth, " +
            "SUM(b.final_amount) AS totalRevenue, " +
            "COUNT(b.id) AS billCount " +
            "FROM bill b " +
            "WHERE b.payment_status = 'PAID' " +
            "AND b.issued_time BETWEEN :startDate AND :endDate " +
            "GROUP BY reportYear, reportMonth " +
            "ORDER BY reportYear ASC, reportMonth ASC", nativeQuery = true)
    List<Object[]> getRevenueReportByMonthFromBills(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = "SELECT " +
            "YEARWEEK(b.issued_time, 1) as reportYearWeek, " +
            "SUM(b.final_amount) AS totalRevenue, " +
            "COUNT(b.id) AS billCount " +
            "FROM bill b " +
            "WHERE b.payment_status = 'PAID' " +
            "AND b.issued_time BETWEEN :startDate AND :endDate " +
            "GROUP BY reportYearWeek " +
            "ORDER BY reportYearWeek ASC", nativeQuery = true)
    List<Object[]> getRevenueReportByWeekFromBills(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("select b from Bill b " +
            "left join fetch b.order o " +
            "left join fetch o.staff u " +
            "order by b.issuedTime desc")
    List<Bill> findAllFetch();
}
