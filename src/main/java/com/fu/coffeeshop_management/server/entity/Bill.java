package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representation of the 'bill' table.
 * Based on the SDD 'bill' table definition.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bill")
public class Bill {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = true)
    private Voucher voucher;

    @Column(name = "points_redeemed")
    private int pointsRedeemed;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

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

    @OneToMany(mappedBy = "bill", fetch = FetchType.LAZY)
    private List<BillPayment> billPayments;
}

