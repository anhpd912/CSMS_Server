package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.Stock;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Inventory Management
 * Handles ingredient and stock operations
 * 
 * Base path: /api/inventory
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Add a new ingredient
     * POST /api/inventory/ingredients
     * Manager only
     */
    @PostMapping("/ingredients")
    public ResponseEntity<IngredientResponse> addIngredient(@Valid @RequestBody IngredientRequest request) {
        log.info("POST /api/inventory/ingredients - Adding new ingredient: {}", request.getName());
        User currentUser = getCurrentUser();
        IngredientResponse response = inventoryService.addIngredient(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing ingredient
     * PUT /api/inventory/ingredients/{id}
     * Manager only
     */
    @PutMapping("/ingredients/{id}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @PathVariable("id") UUID ingredientId,
            @Valid @RequestBody IngredientRequest request) {
        log.info("PUT /api/inventory/ingredients/{} - Updating ingredient", ingredientId);
        User currentUser = getCurrentUser();
        IngredientResponse response = inventoryService.updateIngredient(ingredientId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an ingredient (soft delete)
     * DELETE /api/inventory/ingredients/{id}
     * Manager only
     */
    @DeleteMapping("/ingredients/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable("id") UUID ingredientId) {
        log.info("DELETE /api/inventory/ingredients/{} - Deleting ingredient", ingredientId);
        User currentUser = getCurrentUser();
        inventoryService.deleteIngredient(ingredientId, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get a single ingredient by ID
     * GET /api/inventory/ingredients/{id}
     */
    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientResponse> getIngredient(@PathVariable("id") UUID ingredientId) {
        log.info("GET /api/inventory/ingredients/{} - Fetching ingredient", ingredientId);
        IngredientResponse response = inventoryService.getIngredient(ingredientId);
        return ResponseEntity.ok(response);
    }

    /**
     * List all active ingredients
     * GET /api/inventory/ingredients
     */
    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientResponse>> listIngredients() {
        log.info("GET /api/inventory/ingredients - Listing all ingredients");
        List<IngredientResponse> response = inventoryService.listIngredients();
        return ResponseEntity.ok(response);
    }

    /**
     * Search ingredients by name
     * GET /api/inventory/ingredients/search?name=xxx
     */
    @GetMapping("/ingredients/search")
    public ResponseEntity<List<IngredientResponse>> searchIngredients(@RequestParam("name") String name) {
        log.info("GET /api/inventory/ingredients/search?name={} - Searching ingredients", name);
        List<IngredientResponse> response = inventoryService.searchIngredients(name);
        return ResponseEntity.ok(response);
    }

    /**
     * Add incoming stock transaction
     * POST /api/inventory/stock/incoming
     * Cashier and Manager can perform this
     */
    @PostMapping("/stock/incoming")
    public ResponseEntity<StockTransactionResponse> addIncomingStock(@Valid @RequestBody IncomingStockRequest request) {
        log.info("POST /api/inventory/stock/incoming - Adding stock for product: {}", request.getProductId());
        User currentUser = getCurrentUser();
        StockTransactionResponse response = inventoryService.addIncomingStock(request, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Get stock level for a product
     * GET /api/inventory/stock/{productId}
     */
    @GetMapping("/stock/{productId}")
    public ResponseEntity<Stock> getStockLevel(@PathVariable("productId") UUID productId) {
        log.info("GET /api/inventory/stock/{} - Fetching stock level", productId);
        Stock stock = inventoryService.getStockLevel(productId);
        return ResponseEntity.ok(stock);
    }

    /**
     * Get transaction history for a product
     * GET /api/inventory/transactions/{productId}
     */
    @GetMapping("/transactions/{productId}")
    public ResponseEntity<List<StockTransactionResponse>> getTransactionHistory(@PathVariable("productId") UUID productId) {
        log.info("GET /api/inventory/transactions/{} - Fetching transaction history", productId);
        List<StockTransactionResponse> response = inventoryService.getTransactionHistory(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get low stock notifications
     * GET /api/inventory/low-stock
     * Manager only
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<LowStockResponse>> getLowStockNotifications() {
        log.info("GET /api/inventory/low-stock - Fetching low stock notifications");
        User currentUser = getCurrentUser();
        List<LowStockResponse> response = inventoryService.getLowStockNotifications(currentUser);
        return ResponseEntity.ok(response);
    }

    // === Private Helper Methods ===

    /**
     * Get the currently authenticated user from the security context
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    // === Exception Handlers ===

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error in InventoryController", ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                                                "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Error response DTO
    private record ErrorResponse(int status, String message) {}
}
