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
 * Entity representation of the 'bill_payment' table.
 * Based on the SDD 'bill_payment' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bill_payment")
public class BillPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod; // e.g., "Cash", "QR"

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "paid_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime paidAt = LocalDateTime.now();

    // --- Relationships ---

    // Foreign Key: bill_id -> bill.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;
}
