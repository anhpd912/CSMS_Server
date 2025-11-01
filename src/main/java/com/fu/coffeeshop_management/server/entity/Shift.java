package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representation of the 'shift' table.
 * Based on the SDD 'shift' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "opening_cash", precision = 10, scale = 2, nullable = false)
    private BigDecimal openingCash;

    @Column(name = "closing_cash", precision = 10, scale = 2)
    private BigDecimal closingCash;

    @Column(name = "calculated_cash", precision = 10, scale = 2)
    private BigDecimal calculatedCash; // Sum of cash payments during shift

    @Column(name = "status", length = 50, nullable = false)
    private String status; // e.g., "Open", "Closed"

    @Column(name = "start_time", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // --- Relationships ---

    // Foreign Key: user_id -> user.id (Cashier/Staff for the shift)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
