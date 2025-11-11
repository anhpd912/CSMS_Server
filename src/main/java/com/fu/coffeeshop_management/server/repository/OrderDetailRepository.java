package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    /**
     * Finds all detail items for a specific order.
     */
    List<OrderDetail> findByOrderId(UUID orderId);

    @Query("SELECT " +
            "p.name AS itemName, " +
            "SUM(od.quantity) AS totalUnit, " +
            "SUM(od.price * od.quantity) AS totalRevenue " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.name " +
            "ORDER BY totalUnit DESC " +
            "LIMIT 3")
    List<Object[]> findTopSellingItems(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT " +
            "p.name AS itemName, " +
            "SUM(od.quantity) AS totalUnit, " +
            "SUM(od.price * od.quantity) AS totalRevenue " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.name " +
            "ORDER BY totalUnit ASC " +
            "LIMIT 3")
    List<Object[]> findBottomSellingItems(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
