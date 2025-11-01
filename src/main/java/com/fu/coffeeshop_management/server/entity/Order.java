package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representation of the 'order' table.
 * Based on the SDD 'order' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "status", length = 50, nullable = false)
    private String status; // e.g., "Pending", "Confirmed", "Completed", "Cancelled"

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- Relationships ---

    // Foreign Key: table_id -> table_info.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private TableInfo table;

    // Foreign Key: user_id -> user.id (The waiter who created the order)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // An order has many items (order_details)
    // Cascade.ALL means if we delete an order, delete its details too.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OrderDetail> orderDetails;

    // An order has one bill
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Bill bill;
}
