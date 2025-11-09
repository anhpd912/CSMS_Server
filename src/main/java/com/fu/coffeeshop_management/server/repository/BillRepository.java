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
}
