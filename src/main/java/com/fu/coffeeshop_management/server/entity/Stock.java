package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity representation of the 'stock' table.
 * Based on the SDD (table #19).
 * This holds the inventory level for a product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "quantity_in_stock", nullable = false)
    @Builder.Default
    private Integer quantityInStock = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    // --- Relationships ---

    // Maps the 'product_id' PK to the Product entity
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
}

