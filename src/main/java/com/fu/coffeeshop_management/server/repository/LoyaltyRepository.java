package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Loyalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyRepository extends JpaRepository<Loyalty, UUID> {
    /**
     * Finds the loyalty account for a specific user.
     */
    Optional<Loyalty> findByUserId(UUID userId);
}
