package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.*;
import com.fu.coffeeshop_management.server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Inventory Management operations
 * Handles ingredient (product) and stock management with business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private static final String INGREDIENT_CATEGORY = "Ingredient";
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_INACTIVE = "inactive";
    private static final String TRANSACTION_TYPE_INCOMING = "INCOMING";
    private static final String TRANSACTION_TYPE_OUTGOING = "OUTGOING";
    private static final String TRANSACTION_TYPE_ADJUSTMENT = "ADJUSTMENT";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    /**
     * Add a new ingredient (product with category "Ingredient")
     * Manager only
     */
    @Transactional
    public IngredientResponse addIngredient(IngredientRequest request, User currentUser) {
        log.info("Adding new ingredient: {} by user: {}", request.getName(), currentUser.getUsername());

        // Verify user has MANAGER role
        if (!hasRole(currentUser, "MANAGER")) {
            throw new IllegalStateException("Only managers can add ingredients");
        }

        // Get or create Ingredient category
        Category ingredientCategory = categoryRepository.findByName(INGREDIENT_CATEGORY)
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .name(INGREDIENT_CATEGORY)
                            .description("Ingredients for coffee shop products")
                            .build();
                    return categoryRepository.save(newCategory);
                });

        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageLink(request.getImageLink())
                .status(request.getStatus() != null ? request.getStatus() : STATUS_ACTIVE)
                .category(ingredientCategory)
                .build();

        Product savedProduct = productRepository.save(product);

        // Create initial stock record
        Stock stock = Stock.builder()
                .product(savedProduct)  // @MapsId will use product's ID automatically
                .quantityInStock(0)
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 10)
                .unit(request.getUnit())
                .build();

        stockRepository.save(stock);

        log.info("Ingredient added successfully with ID: {}", savedProduct.getId());
        return mapToIngredientResponse(savedProduct, stock);
    }

    /**
     * Update an existing ingredient
     * Manager only
     */
    @Transactional
    public IngredientResponse updateIngredient(UUID ingredientId, IngredientRequest request, User currentUser) {
        log.info("Updating ingredient: {} by user: {}", ingredientId, currentUser.getUsername());

        // Verify user has MANAGER role
        if (!hasRole(currentUser, "MANAGER")) {
            throw new IllegalStateException("Only managers can update ingredients");
        }

        // Find product
        Product product = productRepository.findByIdWithCategoryAndStock(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with ID: " + ingredientId));

        // Verify it's an ingredient
        if (!INGREDIENT_CATEGORY.equals(product.getCategory().getName())) {
            throw new IllegalArgumentException("Product is not an ingredient");
        }

        // Update product fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageLink(request.getImageLink());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        Product updatedProduct = productRepository.save(product);

        // Update stock reorder level if provided
        Stock stock = product.getStock();
        if (stock != null && request.getReorderLevel() != null) {
            stock.setReorderLevel(request.getReorderLevel());
            stockRepository.save(stock);
        }
        if (stock != null && request.getUnit() != null) {
            stock.setUnit(request.getUnit());
            stockRepository.save(stock);
        }

        log.info("Ingredient updated successfully: {}", ingredientId);
        return mapToIngredientResponse(updatedProduct, stock);
    }

    /**
     * Delete (soft delete) an ingredient
     * Manager only
     */
    @Transactional
    public void deleteIngredient(UUID ingredientId, User currentUser) {
        log.info("Deleting ingredient: {} by user: {}", ingredientId, currentUser.getUsername());

        // Verify user has MANAGER role
        if (!hasRole(currentUser, "MANAGER")) {
            throw new IllegalStateException("Only managers can delete ingredients");
        }

        // Find product
        Product product = productRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with ID: " + ingredientId));

        // Verify it's an ingredient
        if (!INGREDIENT_CATEGORY.equals(product.getCategory().getName())) {
            throw new IllegalArgumentException("Product is not an ingredient");
        }

        // Soft delete by setting status to inactive
        product.setStatus(STATUS_INACTIVE);
        productRepository.save(product);

        log.info("Ingredient soft deleted successfully: {}", ingredientId);
    }

    /**
     * Get a single ingredient by ID
     */
    @Transactional(readOnly = true)
    public IngredientResponse getIngredient(UUID ingredientId) {
        log.info("Fetching ingredient: {}", ingredientId);

        Product product = productRepository.findByIdWithCategoryAndStock(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with ID: " + ingredientId));

        if (!INGREDIENT_CATEGORY.equals(product.getCategory().getName())) {
            throw new IllegalArgumentException("Product is not an ingredient");
        }

        return mapToIngredientResponse(product, product.getStock());
    }

    /**
     * List all active ingredients
     */
    @Transactional(readOnly = true)
    public List<IngredientResponse> listIngredients() {
        log.info("Listing all active ingredients");

        List<Product> ingredients = productRepository.findIngredientsByStatus(STATUS_ACTIVE);

        return ingredients.stream()
                .map(product -> {
                    Stock stock = stockRepository.findById(product.getId()).orElse(null);
                    return mapToIngredientResponse(product, stock);
                })
                .collect(Collectors.toList());
    }

    /**
     * Search ingredients by name
     */
    @Transactional(readOnly = true)
    public List<IngredientResponse> searchIngredients(String name) {
        log.info("Searching ingredients by name: {}", name);

        List<Product> ingredients = productRepository.searchIngredientsByName(name);

        return ingredients.stream()
                .map(product -> {
                    Stock stock = stockRepository.findById(product.getId()).orElse(null);
                    return mapToIngredientResponse(product, stock);
                })
                .collect(Collectors.toList());
    }

    /**
     * Add incoming stock transaction
     * Cashier and Manager can perform this
     */
    @Transactional
    public StockTransactionResponse addIncomingStock(IncomingStockRequest request, User currentUser) {
        log.info("Adding stock transaction for product: {} by user: {}", request.getProductId(), currentUser.getUsername());

        // Verify user has CASHIER or MANAGER role
        if (!hasRole(currentUser, "CASHIER") && !hasRole(currentUser, "MANAGER")) {
            throw new IllegalStateException("Only cashiers and managers can add stock");
        }

        // Find product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));

        // Create inventory transaction
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setQuantity(request.getQuantity());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setUser(currentUser);

        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(transaction);

        // Update stock levels
        Stock stock = stockRepository.findById(product.getId())
                .orElseGet(() -> {
                    // Create stock if doesn't exist
                    Stock newStock = Stock.builder()
                            .productId(product.getId())
                            .product(product)
                            .quantityInStock(0)
                            .reorderLevel(10)
                            .build();
                    return stockRepository.save(newStock);
                });

        // Update quantity based on transaction type
        int newQuantity = stock.getQuantityInStock();
        switch (request.getTransactionType()) {
            case TRANSACTION_TYPE_INCOMING:
                newQuantity += request.getQuantity();
                break;
            case TRANSACTION_TYPE_OUTGOING:
                newQuantity -= request.getQuantity();
                if (newQuantity < 0) {
                    throw new IllegalStateException("Insufficient stock. Current: " + stock.getQuantityInStock());
                }
                break;
            case TRANSACTION_TYPE_ADJUSTMENT:
                newQuantity = request.getQuantity();
                break;
        }

        stock.setQuantityInStock(newQuantity);
        Stock updatedStock = stockRepository.save(stock);

        log.info("Stock updated. Product: {}, Old: {}, New: {}", product.getId(), 
                 stock.getQuantityInStock(), newQuantity);

        return mapToStockTransactionResponse(savedTransaction, updatedStock.getQuantityInStock());
    }

    /**
     * Get stock level for a product
     */
    @Transactional(readOnly = true)
    public Stock getStockLevel(UUID productId) {
        log.info("Fetching stock level for product: {}", productId);

        return stockRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found for product: " + productId));
    }

    /**
     * Get transaction history for a product
     */
    @Transactional(readOnly = true)
    public List<StockTransactionResponse> getTransactionHistory(UUID productId) {
        log.info("Fetching transaction history for product: {}", productId);

        List<InventoryTransaction> transactions = inventoryTransactionRepository
                .findByProductIdOrderByTransactionTimeDesc(productId);

        return transactions.stream()
                .map(transaction -> mapToStockTransactionResponse(transaction, null))
                .collect(Collectors.toList());
    }

    /**
     * Get low stock notifications
     * Manager only
     */
    @Transactional(readOnly = true)
    public List<LowStockResponse> getLowStockNotifications(User currentUser) {
        log.info("Fetching low stock notifications by user: {}", currentUser.getUsername());

        // Verify user has MANAGER role
        if (!hasRole(currentUser, "MANAGER")) {
            throw new IllegalStateException("Only managers can view low stock notifications");
        }

        List<Stock> lowStockItems = stockRepository.findLowStockItems();

        return lowStockItems.stream()
                .map(stock -> {
                    Product product = stock.getProduct();
                    
                    // Only include ingredients
                    if (product != null && INGREDIENT_CATEGORY.equals(product.getCategory().getName())) {
                        int quantityNeeded = stock.getReorderLevel() - stock.getQuantityInStock();
                        BigDecimal estimatedCost = product.getPrice().multiply(new BigDecimal(quantityNeeded));

                        return LowStockResponse.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .currentStock(stock.getQuantityInStock())
                                .reorderLevel(stock.getReorderLevel())
                                .quantityNeeded(quantityNeeded)
                                .estimatedCost(estimatedCost)
                                .status(product.getStatus())
                                .build();
                    }
                    return null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    // === Private Helper Methods ===

    private IngredientResponse mapToIngredientResponse(Product product, Stock stock) {
        boolean isLowStock = false;
        Integer quantityInStock = null;
        Integer reorderLevel = null;
        String unit = null;

        if (stock != null) {
            quantityInStock = stock.getQuantityInStock();
            reorderLevel = stock.getReorderLevel();
            unit = stock.getUnit();
            isLowStock = stock.getReorderLevel() != null && 
                         stock.getQuantityInStock() <= stock.getReorderLevel();
        }

        return IngredientResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageLink(product.getImageLink())
                .status(product.getStatus())
                .categoryName(product.getCategory().getName())
                .quantityInStock(quantityInStock)
                .reorderLevel(reorderLevel)
                .unit(unit)
                .isLowStock(isLowStock)
                .build();
    }

    private StockTransactionResponse mapToStockTransactionResponse(InventoryTransaction transaction, Integer stockLevelAfter) {
        return StockTransactionResponse.builder()
                .id(transaction.getId())
                .productId(transaction.getProduct().getId())
                .productName(transaction.getProduct().getName())
                .quantity(transaction.getQuantity())
                .transactionType(transaction.getTransactionType())
                .transactionTime(transaction.getTransactionTime())
                .userName(transaction.getUser().getUsername())
                .stockLevelAfter(stockLevelAfter)
                .build();
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRole() != null && user.getRole().getName().equals(roleName);
    }
}
