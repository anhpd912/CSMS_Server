package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "payroll_deduction")
public class PayrollDeduction {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal amount;
}

