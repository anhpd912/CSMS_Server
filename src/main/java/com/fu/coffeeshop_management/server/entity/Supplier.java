package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "supplier_id", updatable = false, nullable = false)
    private UUID supplierId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;
}
