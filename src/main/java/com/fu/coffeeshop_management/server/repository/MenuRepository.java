package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID>{
    Optional<Menu> findByName(String name);
}
