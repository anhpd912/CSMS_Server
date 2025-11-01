package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.PayrollDeduction;
import com.fu.coffeeshop_management.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, UUID> {
    List<PayrollDeduction> findByUser(User user);
}
