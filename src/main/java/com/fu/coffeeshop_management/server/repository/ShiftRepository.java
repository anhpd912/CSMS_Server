package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /**
     * Find all shifts for a specific user
     */
    Page<Shift> findByUserIdOrderByStartTimeDesc(UUID userId, Pageable pageable);

    /**
     * Find all shifts with pagination and sorting
     */
    Page<Shift> findAllByOrderByStartTimeDesc(Pageable pageable);

    /**
     * Find shifts by status with pagination
     */
    Page<Shift> findByStatusOrderByStartTimeDesc(String status, Pageable pageable);

    /**
     * Find shifts by user and status with pagination
     */
    Page<Shift> findByUserIdAndStatusOrderByStartTimeDesc(UUID userId, String status, Pageable pageable);

    /**
     * Find shifts within a date range
     */
    @Query("SELECT s FROM Shift s WHERE s.startTime >= :startDate AND s.startTime <= :endDate ORDER BY s.startTime DESC")
    Page<Shift> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate, 
                                Pageable pageable);

    /**
     * Find shifts by user within a date range
     */
    @Query("SELECT s FROM Shift s WHERE s.user.id = :userId AND s.startTime >= :startDate AND s.startTime <= :endDate ORDER BY s.startTime DESC")
    Page<Shift> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Find shifts by status within a date range
     */
    @Query("SELECT s FROM Shift s WHERE s.status = :status AND s.startTime >= :startDate AND s.startTime <= :endDate ORDER BY s.startTime DESC")
    Page<Shift> findByStatusAndDateRange(@Param("status") String status,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Count shifts by status within date range
     */
    @Query("SELECT COUNT(s) FROM Shift s WHERE s.status = :status AND s.startTime >= :startDate AND s.startTime <= :endDate")
    long countByStatusAndDateRange(@Param("status") String status,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
}
