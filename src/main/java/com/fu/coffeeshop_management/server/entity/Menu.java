package com.fu.coffeeshop_management.server.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "menu")
    private Set<MenuProduct> menuProducts;
}
