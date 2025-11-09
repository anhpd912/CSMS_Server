package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.ReservationResponse; // Changed to ReservationResponse
import com.fu.coffeeshop_management.server.dto.ReservationRequest; // Changed to ReservationRequest
import com.fu.coffeeshop_management.server.entity.Reservation;
import com.fu.coffeeshop_management.server.entity.TableInfo;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.ReservationRepository;
import com.fu.coffeeshop_management.server.repository.TableInfoRepository;
import com.fu.coffeeshop_management.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TableInfoRepository tableInfoRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TableInfoRepository tableInfoRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.tableInfoRepository = tableInfoRepository;
        this.userRepository = userRepository;
    }

    // Helper method to convert Reservation entity to ReservationResponse
    private ReservationResponse convertToDTO(Reservation reservation) { // Renamed to convertToResponse
        return ReservationResponse.builder()
                .id(reservation.getId())
                .customerName(reservation.getCustomerName())
                .customerPhone(reservation.getCustomerPhone())
                .reservationTime(reservation.getReservationTime())
                .numGuests(reservation.getNumGuests())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .tableId(reservation.getTable() != null ? reservation.getTable().getId() : null)
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .build();
    }

    public List<ReservationResponse> listReservations() { // Changed to ReservationResponse
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> listReservationsByTable(UUID tableId) { // Changed to ReservationResponse
        return reservationRepository.findByTableId(tableId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest requestDTO) { // Changed to ReservationRequest and ReservationResponse
        TableInfo table = tableInfoRepository.findById(requestDTO.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + requestDTO.getTableId()));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDTO.getUserId()));

        Reservation reservation = Reservation.builder()
                .customerName(requestDTO.getCustomerName())
                .customerPhone(requestDTO.getCustomerPhone())
                .reservationTime(requestDTO.getReservationTime())
                .numGuests(requestDTO.getNumGuests())
                .status(requestDTO.getStatus())
                .table(table)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDTO(savedReservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(UUID id) { // Changed to ReservationResponse
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));

        reservation.setStatus("Cancelled");
        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDTO(updatedReservation);
    }
}
