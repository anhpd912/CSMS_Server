package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Supplier findByName(String name);
}
