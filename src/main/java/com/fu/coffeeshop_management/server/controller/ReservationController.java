package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.entity.Reservation;
import com.fu.coffeeshop_management.server.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID; // Import UUID

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reservation> listReservations() {
        return service.listReservations();
    }

    @GetMapping("/table/{tableId}") // New endpoint for reservations by table
    public List<Reservation> listReservationsByTable(@PathVariable UUID tableId) {
        return service.listReservationsByTable(tableId);
    }
}