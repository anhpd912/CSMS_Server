package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Finds a role by its name.
     *
     * @param name The name of the role (e.g., "MANAGER", "CASHIER").
     * @return An Optional containing the Role if found.
     */
    Optional<Role> findByName(String name);
}
