package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByTableId(UUID tableId); // New method to find reservations by table ID
}
