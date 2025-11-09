package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.ReservationDTO; // Import ReservationDTO
import com.fu.coffeeshop_management.server.entity.Reservation;
import com.fu.coffeeshop_management.server.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // Import Collectors

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Helper method to convert Reservation entity to ReservationDTO
    private ReservationDTO convertToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .customerName(reservation.getCustomerName())
                .customerPhone(reservation.getCustomerPhone())
                .reservationTime(reservation.getReservationTime())
                .numGuests(reservation.getNumGuests())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .tableId(reservation.getTable() != null ? reservation.getTable().getId() : null) // Get table ID
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)     // Get user ID
                .build();
    }

    public List<ReservationDTO> listReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> listReservationsByTable(UUID tableId) {
        return reservationRepository.findByTableId(tableId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
