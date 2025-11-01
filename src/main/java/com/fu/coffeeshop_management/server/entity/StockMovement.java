package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representation of the 'stock_movement' table.
 * Based on the SDD 'stock_movement' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_transaction")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", length = 36, nullable = false, updatable = false) // Matched SDD
    private UUID id;

    @Column(name = "quantity", nullable = false) // Matched SDD
    private Integer quantity; // Positive for import, negative for export/sale

    @Column(name = "transaction_type", length = 20, nullable = false) // Matched SDD
    private String transactionType; // e.g., "import", "export"

    @Column(name = "transaction_time", nullable = false, updatable = false) // Matched SDD
    @Builder.Default
    private LocalDateTime transactionTime = LocalDateTime.now();

    // --- Relationships ---

    // Foreign Key: item_id -> item.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Changed from item_id to product_id
    private Product product; // Changed from Item to Product

    // Foreign Key: user_id -> user.id (Staff who recorded the movement)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
