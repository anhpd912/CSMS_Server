package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    /**
     * Finds all orders with a specific status (e.g., "pending", "completed").
     */
    List<Order> findByStatus(String status);

    /**
     * Finds all orders created by a specific staff member.
     */
    List<Order> findByStaffId(UUID staffId);
}
