package com.fu.coffeeshop_management.server.config;

import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import com.fu.coffeeshop_management.server.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This component runs on application startup and seeds the database
 * with essential data, like user roles. This is more robust than data.sql.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedManager();
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

    private void seedManager() {
        if (userRepository.findByEmail("manager@coffeeshop.com").isEmpty()) {
            userRepository.save(User.builder().email("manager@coffeeshop.com")
                    .password(new BCryptPasswordEncoder().encode("Manager123"))
                    .role(roleRepository.findByName("MANAGER").get())
                    .mobile("0123456780")
                    .fullname("Manager")
                    .build()
            );
        }
    }
}
