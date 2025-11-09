package com.fu.coffeeshop_management.server.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representation of the 'bill' table.
 * Based on the SDD 'bill' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bill")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Bill {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(name = "discount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal discount;

    @Column(name = "tax", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal tax;

    @Column(name = "final_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal finalAmount;

    @Column(name = "issued_time", nullable = false)
    private LocalDateTime issuedTime;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

}
