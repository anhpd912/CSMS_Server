package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity representation of the 'loyalty' table.
 * Based on the SDD 'loyalty' table definition.
 * Note: Your SDD shows loyalty_id in both user and loyalty.
 * A cleaner design is to have 'user.id' as the PK/FK.
 * But I will follow the SDD.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty")
public class Loyalty {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "loyalty_id", length = 36, nullable = false, updatable = false)
    private UUID loyaltyId;

    @Column(name = "points_balance", nullable = false)
    @Builder.Default
    private Integer points = 0;

    @Column(name = "tier", length = 50)
    private String tier; // e.g., "Bronze", "Silver", "Gold"

    // --- Relationships ---

    // This defines the "other side" of the relationship
    // A loyalty account is tied to one user.
    @OneToOne(mappedBy = "loyalty")
    private User user;
}
