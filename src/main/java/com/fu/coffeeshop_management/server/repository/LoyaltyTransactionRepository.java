package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Loyalty;
import com.fu.coffeeshop_management.server.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {
    List<LoyaltyTransaction> findByLoyalty(Loyalty loyalty);
}
