package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Bill;
import com.fu.coffeeshop_management.server.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, UUID> {
    List<BillPayment> findByBill(Bill bill);
    List<BillPayment> findByBillId(UUID billId);
    @Transactional(readOnly = true)
    List<BillPayment> findByBill_IdIn(List<UUID> billIds);
}
