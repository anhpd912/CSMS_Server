package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the User entity.
 * Based on the SDD 'UserRepository (Server)' class specification.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email (which is used as the username).
     * This implements the 'findByUsername' method from the SDD.
     *
     * @param email The user's email address.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The user's email address.
     * @return true if a user with this email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
