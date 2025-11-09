package com.fu.coffeeshop_management.server.config;

import com.fu.coffeeshop_management.server.entity.Category;
import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.entity.Product;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.CategoryRepository;
import com.fu.coffeeshop_management.server.repository.ProductRepository;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import com.fu.coffeeshop_management.server.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * This component runs on application startup and seeds the database
 * with essential data, like user roles. This is more robust than data.sql.
 */
@Component
public class DataSeeder /*implements CommandLineRunner*/ {

    /*private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedManager();
        seedCategoryProduct();
        seedProducts();
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
                    .name("JUICE")
                    .build());
        }
        if (categoryRepository.findByName("FAST FOOD").isEmpty()) {
            categoryRepository.save(Category.builder()
                    .description("Fast food")
                    .name("FAST FOOD")
                    .build());
        }
    }

    private void seedProducts() {
        // Coffee
        Category coffeeCategory = categoryRepository.findByName("COFFEE").orElse(null);
        if (coffeeCategory != null) {
            if (productRepository.findByName("Espresso").isEmpty()) {
                productRepository.save(Product.builder().name("Espresso").description("Strong black coffee").price(new BigDecimal("35000.00")).imageLink("https://example.com/images/espresso.jpg").category(coffeeCategory).build());
            }
            if (productRepository.findByName("Latte").isEmpty()) {
                productRepository.save(Product.builder().name("Latte").description("Coffee with steamed milk").price(new BigDecimal("45000.00")).imageLink("https://example.com/images/latte.jpg").category(coffeeCategory).build());
            }
            if (productRepository.findByName("Cappuccino").isEmpty()) {
                productRepository.save(Product.builder().name("Cappuccino").description("Espresso with a thick layer of milk foam").price(new BigDecimal("45000.00")).imageLink("https://example.com/images/cappuccino.jpg").category(coffeeCategory).build());
            }
        }

        // Bubble Tea
        Category bubbleTeaCategory = categoryRepository.findByName("BUBBLE TEA").orElse(null);
        if (bubbleTeaCategory != null) {
            if (productRepository.findByName("Black Sugar Bubble Tea").isEmpty()) {
                productRepository.save(Product.builder().name("Black Sugar Bubble Tea").description("Fresh milk with black sugar and pearls").price(new BigDecimal("50000.00")).imageLink("https://example.com/images/black-sugar-bubble-tea.jpg").category(bubbleTeaCategory).build());
            }
            if (productRepository.findByName("Taro Bubble Tea").isEmpty()) {
                productRepository.save(Product.builder().name("Taro Bubble Tea").description("Sweet taro flavored milk tea").price(new BigDecimal("48000.00")).imageLink("https://example.com/images/taro-bubble-tea.jpg").category(bubbleTeaCategory).build());
            }
        }

        // Tea
        Category teaCategory = categoryRepository.findByName("TEA").orElse(null);
        if (teaCategory != null) {
            if (productRepository.findByName("Peach Tea").isEmpty()) {
                productRepository.save(Product.builder().name("Peach Tea").description("Refreshing peach flavored iced tea").price(new BigDecimal("40000.00")).imageLink("https://example.com/images/peach-tea.jpg").category(teaCategory).build());
            }
            if (productRepository.findByName("Lychee Tea").isEmpty()) {
                productRepository.save(Product.builder().name("Lychee Tea").description("Sweet lychee flavored iced tea").price(new BigDecimal("42000.00")).imageLink("https://example.com/images/lychee-tea.jpg").category(teaCategory).build());
            }
        }

        // Juice
        Category juiceCategory = categoryRepository.findByName("JUICE").orElse(null);
        if (juiceCategory != null) {
            if (productRepository.findByName("Orange Juice").isEmpty()) {
                productRepository.save(Product.builder().name("Orange Juice").description("Freshly squeezed orange juice").price(new BigDecimal("38000.00")).imageLink("https://example.com/images/orange-juice.jpg").category(juiceCategory).build());
            }
            if (productRepository.findByName("Watermelon Juice").isEmpty()) {
                productRepository.save(Product.builder().name("Watermelon Juice").description("Fresh watermelon juice").price(new BigDecimal("35000.00")).imageLink("https://example.com/images/watermelon-juice.jpg").category(juiceCategory).build());
            }
        }

        // Fast Food
        Category fastFoodCategory = categoryRepository.findByName("FAST FOOD").orElse(null);
        if (fastFoodCategory != null) {
            if (productRepository.findByName("French Fries").isEmpty()) {
                productRepository.save(Product.builder().name("French Fries").description("Crispy salted french fries").price(new BigDecimal("30000.00")).imageLink("https://example.com/images/french-fries.jpg").category(fastFoodCategory).build());
            }
            if (productRepository.findByName("Chicken Nuggets").isEmpty()) {
                productRepository.save(Product.builder().name("Chicken Nuggets").description("Deep fried chicken nuggets").price(new BigDecimal("45000.00")).imageLink("https://example.com/images/chicken-nuggets.jpg").category(fastFoodCategory).build());
            }
        }
    }*/
}
