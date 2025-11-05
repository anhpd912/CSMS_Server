package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.CashTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CashTransactionRepository extends JpaRepository<CashTransaction, UUID> {
    
    /**
     * Find all cash transactions for a specific shift
     */
    List<CashTransaction> findByShiftIdOrderByTimestampAsc(UUID shiftId);

    /**
     * Count transactions for a specific shift
     */
    long countByShiftId(UUID shiftId);

    /**
     * Get total amount by transaction type for a shift
     */
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CashTransaction ct " +
           "WHERE ct.shift.id = :shiftId AND ct.transactionType = :type")
    BigDecimal sumAmountByShiftIdAndType(@Param("shiftId") UUID shiftId, 
                                         @Param("type") String transactionType);

    /**
     * Count transactions by type for a shift
     */
    @Query("SELECT COUNT(ct) FROM CashTransaction ct " +
           "WHERE ct.shift.id = :shiftId AND ct.transactionType = :type")
    long countByShiftIdAndType(@Param("shiftId") UUID shiftId, 
                               @Param("type") String transactionType);
}
