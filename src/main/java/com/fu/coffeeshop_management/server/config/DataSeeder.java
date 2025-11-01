package com.fu.coffeeshop_management.server.config;

import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This component runs on application startup and seeds the database
 * with essential data, like user roles. This is more robust than data.sql.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

    private void seedRoles() {
        if (roleRepository.findByName("MANAGER").isEmpty()) {
            roleRepository.save(Role.builder().name("MANAGER").build());
        }
        if (roleRepository.findByName("CASHIER").isEmpty()) {
            roleRepository.save(Role.builder().name("CASHIER").build());
        }
        if (roleRepository.findByName("WAITER").isEmpty()) {
            roleRepository.save(Role.builder().name("WAITER").build());
        }
    }
}
