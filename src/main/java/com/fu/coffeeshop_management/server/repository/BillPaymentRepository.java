package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, UUID> {
    /**
     * Finds all payment records for a specific bill.
     */
    List<BillPayment> findByBillId(UUID billId);
}
