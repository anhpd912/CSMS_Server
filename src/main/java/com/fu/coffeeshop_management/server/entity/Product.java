package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representation of the 'product' table.
 * Based on the SDD (table #06).
 * This was previously named Item.java.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "image_link", length = 255) // Renamed from imageUrl to match SDD
    private String imageLink;

    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private String status = "active"; // Maps to product.status (active/inactive)

    // --- Relationships ---

    // Foreign Key: category_id -> category.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // A product can be in many order_details
    @OneToMany(mappedBy = "product")
    private Set<OrderDetail> orderDetails;

    // A product can have many stock movements
    @OneToMany(mappedBy = "product")
    private Set<InventoryTransaction> inventoryTransactions;

    // A product has one stock record
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;
}
