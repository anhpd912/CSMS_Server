package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty")
public class Loyalty {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "loyalty_id", nullable = false, updatable = false)
    private UUID loyaltyId;

    @Column(name = "points", nullable = false)
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

