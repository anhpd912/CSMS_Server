package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Order;
import com.fu.coffeeshop_management.server.entity.OrderVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderVoucherRepository extends JpaRepository<OrderVoucher, UUID> {
    List<OrderVoucher> findByOrder(Order order);
}
