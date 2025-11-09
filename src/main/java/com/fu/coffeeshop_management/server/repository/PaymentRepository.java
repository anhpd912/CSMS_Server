package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Bill;
import com.fu.coffeeshop_management.server.entity.Order;
import com.fu.coffeeshop_management.server.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>{
    Optional<Payment> findByBill(Bill bill);
}
