package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.ReservationResponse; // Changed to ReservationResponse
import com.fu.coffeeshop_management.server.dto.ReservationRequest; // Changed to ReservationRequest
import com.fu.coffeeshop_management.server.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReservationResponse> listReservations() { // Changed to ReservationResponse
        return service.listReservations();
    }

    @GetMapping("/table/{tableId}")
    public List<ReservationResponse> listReservationsByTable(@PathVariable UUID tableId) { // Changed to ReservationResponse
        return service.listReservationsByTable(tableId);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest requestDTO) { // Changed to ReservationRequest and ReservationResponse
        ReservationResponse newReservation = service.createReservation(requestDTO);
        return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable UUID id) { // Changed to ReservationResponse
        ReservationResponse cancelledReservation = service.cancelReservation(id);
        return ResponseEntity.ok(cancelledReservation);
    }
}
