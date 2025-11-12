package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.dto.PointsHistoryItem;
import com.fu.coffeeshop_management.server.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {
    @Query("""
        select new com.fu.coffeeshop_management.server.dto.PointsHistoryItem(
            tx.id,
            o.id,
            null,
            case 
              when coalesce(tx.pointsEarned,0) > 0 then 'EARN' 
              when coalesce(tx.pointsSpent,0)  > 0 then 'REDEEM'
              else 'EARN'
            end,
            tx.pointsEarned,
            tx.pointsSpent,
            tx.timestamp
        )
        from LoyaltyTransaction tx
        join tx.loyalty l
        join Customer c on c.loyalty = l
        left join tx.order o
        where c.id = :customerId
        order by tx.timestamp desc
    """)
    List<PointsHistoryItem> findHistoryByCustomerId(@Param("customerId") UUID customerId);
}
