package com.fu.coffeeshop_management.server.config;

import com.fu.coffeeshop_management.server.entity.*;
import com.fu.coffeeshop_management.server.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * This component runs on application startup and seeds the database
 * with essential data, like user roles. This is more robust than data.sql.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TableInfoRepository tableInfoRepository;
    private final ReservationRepository reservationRepository;


    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, CategoryRepository categoryRepository, TableInfoRepository tableInfoRepository, ReservationRepository reservationRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tableInfoRepository = tableInfoRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedManager();
        seedCategoryProduct();
//        seedTableInfo();
//        seedReservation();
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

    private void seedCategoryProduct() {
        if (categoryRepository.findByName("COFFEE").isEmpty()) {
            categoryRepository.save(Category.builder()
                    .description("Coffee drinks")
                    .name("COFFEE")
                    .build());
        }
        if (categoryRepository.findByName("BUBBLE TEA").isEmpty()) {
            categoryRepository.save(Category.builder()
                    .description("Bubble tea lady drinks")
                    .name("BUBBLE TEA")
                    .build());
        }
        if (categoryRepository.findByName("TEA").isEmpty()) {
            categoryRepository.save(Category.builder()
                    .description("Tea drinks")
                    .name("TEA")
                    .build());
        }
        if (categoryRepository.findByName("JUICE").isEmpty()) {
            categoryRepository.save(Category.builder()
                    .description("Juice drinks")
                    .name("Juice")
                    .build());
        }
    }
//
//    private void seedTableInfo() {
//        if (tableInfoRepository.("Table 1").isEmpty()) {
//            tableInfoRepository.save(TableInfo.builder()
//                    .name("Table 1")
//                    .status("Available")
//                    .location("1")
//                    .sheetCount(4)
//                    .build());
//        }
//
//        if (tableInfoRepository.findByName("Table 2").isEmpty()) {
//            tableInfoRepository.save(TableInfo.builder()
//                    .name("Table 2")
//                    .status("Available")
//                    .location("1")
//                    .sheetCount(4)
//                    .build());
//        }
//
//        if (tableInfoRepository.findByName("Table 3").isEmpty()) {
//            tableInfoRepository.save(TableInfo.builder()
//                    .name("Table 3")
//                    .status("Available")
//                    .location("1")
//                    .sheetCount(4)
//                    .build());
//        }
//    }
//
//    private void seedReservation() {
//        if (reservationRepository.findByCustomerName("Neco").isEmpty()) {
//            reservationRepository.save(Reservation.builder()
//                    .customerName("John Doe")
//                    .customerPhone("0123456789")
//                    .reservationTime(LocalDateTime.now())
//                    .numGuests(4)
//                    .status("Pending")
//                    .table(tableInfoRepository.findAll().get(0))
//                    .user(userRepository.findByEmail("manager@coffeeshop.com").get())
//                    .build());
//        }
//
//        if (reservationRepository.findByTableId(tableInfoRepository.findByName("Table 2").get().getId()).isEmpty()) {
//            reservationRepository.save(Reservation.builder()
//                    .customerName("John Doe")
//                    .customerPhone("0123456789")
//                    .reservationTime(LocalDateTime.now())
//                    .numGuests(4)
//                    .status("Pending")
//                    .table(tableInfoRepository.findByName("Table 2").get())
//                    .user(userRepository.findByEmail("manager@coffeeshop.com").get())
//                    .build());
//        }
//
//        if (reservationRepository.findByTableId(tableInfoRepository.findByName("Table 3").get().getId()).isEmpty()) {
//            reservationRepository.save(Reservation.builder()
//                    .customerName("John Doe")
//                    .customerPhone("0123456789")
//                    .reservationTime(LocalDateTime.now())
//                    .numGuests(4)
//                    .status("Pending")
//                    .table(tableInfoRepository.findByName("Table 3").get())
//                    .user(userRepository.findByEmail("manager@coffeeshop.com").get())
//                    .build());
//        }
}
