package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    /**
     * Finds all detail items for a specific order.
     */
    List<OrderDetail> findByOrderId(UUID orderId);
}
