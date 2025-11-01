package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    /**
     * Finds reservations for a specific user.
     */
    List<Reservation> findByUserId(UUID userId);

    List<Reservation> findByCustomer(String customer)

    /**
     * Finds reservations for a specific table.
     */
    List<Reservation> findByTableId(UUID tableId);

    /**
     * Finds all reservations that overlap with a given time range.
     * Useful for checking availability.
     */
    List<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);
}
