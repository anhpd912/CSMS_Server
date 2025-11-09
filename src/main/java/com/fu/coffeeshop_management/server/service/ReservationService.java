package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.entity.Reservation;
import com.fu.coffeeshop_management.server.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID; // Import UUID

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> listReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> listReservationsByTable(UUID tableId) {
        return reservationRepository.findByTableId(tableId);
    }
}
