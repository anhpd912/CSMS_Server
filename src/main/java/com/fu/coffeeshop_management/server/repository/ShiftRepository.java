package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    /**
     * Finds all shifts with a specific status (e.g., "open", "closed").
     */
    List<Shift> findByStatus(String status);

    /**
     * Finds the current open shift for a specific user (cashier).
     */
    Optional<Shift> findByUserIdAndStatus(UUID userId, String status);
}
