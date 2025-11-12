package com.fu.coffeeshop_management.server.config;

import com.fu.coffeeshop_management.server.entity.*;
import com.fu.coffeeshop_management.server.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * This component runs on application startup and seeds the database
 * with essential data, like user roles. This is more robust than data.sql.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_CASHIER = "CASHIER";
    private static final String ROLE_WAITER = "WAITER";

    // Constants for Category names
    private static final String CATEGORY_COFFEE = "COFFEE";
    private static final String CATEGORY_BUBBLE_TEA = "BUBBLE TEA";
    private static final String CATEGORY_TEA = "TEA";
    private static final String CATEGORY_JUICE = "JUICE";
    private static final String CATEGORY_FAST_FOOD = "FAST FOOD";

    // Constants for Table names
    private static final String TABLE_101_NAME = "Bàn 101";
    private static final String TABLE_102_NAME = "Bàn 102";
    private static final String TABLE_201_NAME = "Bàn 201";

    // Constants for User emails
    private static final String MANAGER_EMAIL = "manager@coffeeshop.com";
    private static final String WAITER_EMAIL = "waiter@coffeeshop.com";
    private static final String CASHIER_EMAIL = "cashier@coffeeshop.com";

    // Constants for Status
    private static final String STATUS_AVAILABLE = "Available";
    private static final String STATUS_OCCUPIED = "Occupied";
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_ACTIVE = "active";


    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TableInfoRepository tableInfoRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      TableInfoRepository tableInfoRepository,
                      ProductRepository productRepository,
                      OrderRepository orderRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tableInfoRepository = tableInfoRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedManager();
        seedWaiter();
        seedCashier();
        seedCategoryProduct();
        seedProducts();
        seedTableInfo();
        seedOrdersAndDetails();
    }

    private void seedRoles() {
        if (roleRepository.findByName(ROLE_MANAGER).isEmpty()) {
            roleRepository.save(Role.builder().name(ROLE_MANAGER).build());
        }
        if (roleRepository.findByName(ROLE_CASHIER).isEmpty()) {
            roleRepository.save(Role.builder().name(ROLE_CASHIER).build());
        }
        if (roleRepository.findByName(ROLE_WAITER).isEmpty()) {
            roleRepository.save(Role.builder().name(ROLE_WAITER).build());
        }
    }

    private void seedManager() {
        if (userRepository.findByEmail(MANAGER_EMAIL).isEmpty()) {
            Role managerRole = roleRepository.findByName(ROLE_MANAGER)
                    .orElseThrow(() -> new RuntimeException("MANAGER role not found!"));
            userRepository.save(User.builder().email(MANAGER_EMAIL)
                    .password(new BCryptPasswordEncoder().encode("Manager123"))
                    .role(managerRole)
                    .mobile("0123456780")
                    .fullname("Manager")
                    .build()
            );
        }
    }

    private void seedWaiter() {
        if (userRepository.findByEmail(WAITER_EMAIL).isEmpty()) {
            Role waiterRole = roleRepository.findByName(ROLE_WAITER)
                    .orElseThrow(() -> new RuntimeException("WAITER role not found!"));
            userRepository.save(User.builder().email(WAITER_EMAIL)
                    .password(new BCryptPasswordEncoder().encode("Waiter123"))
                    .role(waiterRole)
                    .mobile("0987654321")
                    .fullname("Nhân Viên Phục Vụ")
                    .build()
            );
        }
    }

    private void seedCashier() {
        if (userRepository.findByEmail(WAITER_EMAIL).isEmpty()) {
            Role cashierRole = roleRepository.findByName(ROLE_CASHIER)
                    .orElseThrow(() -> new RuntimeException("Cashier role not found!"));
            userRepository.save(User.builder().email(CASHIER_EMAIL)
                    .password(new BCryptPasswordEncoder().encode("Cashier123"))
                    .role(cashierRole)
                    .mobile("0981782345")
                    .fullname("Nhân Viên Thu Ngân")
                    .build()
            );
        }
    }

    private void seedCategoryProduct() {
        if (categoryRepository.findByName(CATEGORY_COFFEE).isEmpty()) {
            categoryRepository.save(Category.builder().description("Coffee drinks").name(CATEGORY_COFFEE).build());
        }
        if (categoryRepository.findByName(CATEGORY_BUBBLE_TEA).isEmpty()) {
            categoryRepository.save(Category.builder().description("Bubble tea lady drinks").name(CATEGORY_BUBBLE_TEA).build());
        }
        if (categoryRepository.findByName(CATEGORY_TEA).isEmpty()) {
            categoryRepository.save(Category.builder().description("Tea drinks").name(CATEGORY_TEA).build());
        }
        if (categoryRepository.findByName(CATEGORY_JUICE).isEmpty()) {
            categoryRepository.save(Category.builder().description("Juice drinks").name(CATEGORY_JUICE).build());
        }
        if (categoryRepository.findByName(CATEGORY_FAST_FOOD).isEmpty()) {
            categoryRepository.save(Category.builder().description("Fast food").name(CATEGORY_FAST_FOOD).build());
        }
    }

    private void seedProducts() {
        categoryRepository.findByName(CATEGORY_COFFEE).ifPresent(category -> {
            saveProductWithStock("Espresso", "Strong black coffee", new BigDecimal("35000.00"), "https://example.com/images/espresso.jpg", category, 100);
            saveProductWithStock("Latte", "Coffee with steamed milk", new BigDecimal("45000.00"), "https://example.com/images/latte.jpg", category, 100);
            saveProductWithStock("Cappuccino", "Espresso with a thick layer of milk foam", new BigDecimal("45000.00"), "https://example.com/images/cappuccino.jpg", category, 100);
        });

        categoryRepository.findByName(CATEGORY_BUBBLE_TEA).ifPresent(category -> {
            saveProductWithStock("Black Sugar Bubble Tea", "Fresh milk with black sugar and pearls", new BigDecimal("50000.00"), "https://example.com/images/black-sugar-bubble-tea.jpg", category, 50);
            saveProductWithStock("Taro Bubble Tea", "Sweet taro flavored milk tea", new BigDecimal("48000.00"), "https://example.com/images/taro-bubble-tea.jpg", category, 50);
        });

        categoryRepository.findByName(CATEGORY_TEA).ifPresent(category -> {
            saveProductWithStock("Peach Tea", "Refreshing peach flavored iced tea", new BigDecimal("40000.00"), "https://example.com/images/peach-tea.jpg", category, 80);
        });

        categoryRepository.findByName(CATEGORY_JUICE).ifPresent(category -> {
            saveProductWithStock("Orange Juice", "Freshly squeezed orange juice", new BigDecimal("38000.00"), "https://example.com/images/orange-juice.jpg", category, 70);
        });

        categoryRepository.findByName(CATEGORY_FAST_FOOD).ifPresent(category -> {
            saveProductWithStock("French Fries", "Crispy salted french fries", new BigDecimal("30000.00"), "https://example.com/images/french-fries.jpg", category, 200);
        });
    }

    private void saveProductWithStock(String name, String desc, BigDecimal price, String imgLink, Category category, int initialStock) {
        if (productRepository.findByName(name).isEmpty()) {
            Product product = Product.builder()
                    .name(name)
                    .description(desc)
                    .price(price)
                    .imageLink(imgLink)
                    .category(category)
                    .status(STATUS_ACTIVE)
                    .build();

            Stock stock = Stock.builder()
                    .product(product)
                    .quantityInStock(initialStock)
                    .reorderLevel(10)
                    .build();

            product.setStock(stock);
            productRepository.save(product);
        }
    }

    private void seedTableInfo() {
        if (tableInfoRepository.findByNameContainingIgnoreCase(TABLE_101_NAME).isEmpty()) {
            tableInfoRepository.save(TableInfo.builder().name(TABLE_101_NAME).location("Tầng 1 - Gần cửa").status(STATUS_AVAILABLE).seatCount(4).build());
        }
        if (tableInfoRepository.findByNameContainingIgnoreCase(TABLE_102_NAME).isEmpty()) {
            tableInfoRepository.save(TableInfo.builder().name(TABLE_102_NAME).location("Tầng 1 - Trong góc").status(STATUS_AVAILABLE).seatCount(2).build());
        }
        if (tableInfoRepository.findByNameContainingIgnoreCase(TABLE_201_NAME).isEmpty()) {
            tableInfoRepository.save(TableInfo.builder().name(TABLE_201_NAME).location("Tầng 2 - Ban công").status(STATUS_AVAILABLE).seatCount(6).build());
        }
    }

    private void seedOrdersAndDetails() {
        if (orderRepository.count() > 0) {
            return;
        }

        User waiter = userRepository.findByEmail(WAITER_EMAIL).orElseThrow(() -> new RuntimeException("Waiter user not found!"));

        TableInfo table101 = tableInfoRepository.findByNameContainingIgnoreCase(TABLE_101_NAME).stream().findFirst().orElseThrow(() -> new RuntimeException("Table 101 not found!"));
        TableInfo table102 = tableInfoRepository.findByNameContainingIgnoreCase(TABLE_102_NAME).stream().findFirst().orElseThrow(() -> new RuntimeException("Table 102 not found!"));

        Product latte = productRepository.findByName("Latte").orElseThrow(() -> new RuntimeException("Latte not found!"));
        Product fries = productRepository.findByName("French Fries").orElseThrow(() -> new RuntimeException("French Fries not found!"));
        Product espresso = productRepository.findByName("Espresso").orElseThrow(() -> new RuntimeException("Espresso not found!"));
        Product taroTea = productRepository.findByName("Taro Bubble Tea").orElseThrow(() -> new RuntimeException("Taro Tea not found!"));

        // --- Order 1 ---
        Order order1 = Order.builder()
                .status(STATUS_PENDING)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .updatedAt(LocalDateTime.now().minusMinutes(30))
                .totalPrice(75000.00)
//                .table(table101)
                .staff(waiter)
                .build();

        OrderDetail detail11 = OrderDetail.builder().product(latte).quantity(1).price(latte.getPrice()).build();
        detail11.setOrder(order1); // <-- FIX: Set back-reference

        OrderDetail detail12 = OrderDetail.builder().product(fries).quantity(1).price(fries.getPrice()).build();
        detail12.setOrder(order1); // <-- FIX: Set back-reference

        order1.setOrderDetails(Set.of(detail11, detail12));
        orderRepository.save(order1);

        table101.setStatus(STATUS_OCCUPIED);
        tableInfoRepository.save(table101);

        // --- Order 2 ---
        Order order2 = Order.builder()
                .status(STATUS_PENDING)
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .totalPrice(70000.00)
//                .table(table102)
                .staff(waiter)
                .build();

        OrderDetail detail21 = OrderDetail.builder().product(espresso).quantity(2).price(espresso.getPrice()).build();
        detail21.setOrder(order2); // <-- FIX: Set back-reference

        order2.setOrderDetails(Set.of(detail21));
        orderRepository.save(order2);

        table102.setStatus(STATUS_OCCUPIED);
        tableInfoRepository.save(table102);

        // --- Order 3 ---
        Order order3 = Order.builder()
                .status(STATUS_PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .totalPrice(48000.00)
//                .table(table101) // Another order on the same table
                .staff(waiter)
                .build();

        OrderDetail detail31 = OrderDetail.builder().product(taroTea).quantity(1).price(taroTea.getPrice()).build();
        detail31.setOrder(order3); // <-- FIX: Set back-reference

        order3.setOrderDetails(Set.of(detail31));
        orderRepository.save(order3);
    }
}
