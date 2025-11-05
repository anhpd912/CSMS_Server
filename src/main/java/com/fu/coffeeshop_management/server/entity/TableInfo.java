package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;
/**
 * Entity representation of the 'table_info' table.
 * Based on the SDD 'table_info' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "table_info")
public class TableInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "status", length = 50, nullable = false)
    private String status; // e.g., "Available", "Occupied", "Reserved"

    @Column(name = "location", length = 50, nullable = false)
    private String location; // e.g., "Indoor", "Outdoor", "Balcony"

    @Column(name = "sheet_count", nullable = false)
    private Integer sheetCount;

    // --- Relationships ---

    // A table can have many reservations
    @OneToMany(mappedBy = "table")
    private Set<Reservation> reservations;

    // A table can have many orders
    @OneToMany(mappedBy = "table")
    private Set<Order> orders;
}

