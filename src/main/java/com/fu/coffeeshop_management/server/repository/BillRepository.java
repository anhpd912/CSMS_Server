package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    /**
     * Finds the bill associated with a specific order.
     */
    Optional<Bill> findByOrderId(UUID orderId);
}
