package com.fu.coffeeshop_management.server.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
/**
 * Entity representation of the 'table_info' table.
 * Based on the SDD 'table_info' table definition.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "table_info")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TableInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 16, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "location", length = 50, nullable = false)
    private String location;

    @Column(name = "status", length = 50, nullable = false)
    private String status; // e.g., "Available", "Occupied", "Reserved"

    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    // --- Relationships ---

    // A table can have many reservations
    @OneToMany(mappedBy = "table")
    private Set<Reservation> reservations;

    // A table can have many orders
    @OneToMany(mappedBy = "tableInfo", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<TableOrder> tableOrders = new HashSet<>();
}

