package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.exception.ResourceNotFoundException;
import com.fu.coffeeshop_management.server.exception.UnauthorizedException;
import com.fu.coffeeshop_management.server.service.TableInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class TableInfoController {
    private final TableInfoService service;

    @GetMapping
    public List<TableInfoDTO> listTables(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "keyword") String keyword
    ) {
        return service.listTables(status, keyword);
    }

    /**
     * GET /api/tables
     * Get all tables with optional filtering
     * 
     * Query Parameters:
     * - status: Filter by status (Available, Occupied, Reserved)
     * - location: Filter by location (Indoor, Outdoor, Balcony)
     * - minSheetCount: Filter by minimum seat count
     * - page: Page number (default: 0)
     * - size: Page size (default: 20)
     * - sort: Sort field and direction (e.g., name,asc)
     * 
     * Access: Cashier, Waiter, Manager
     */
    @GetMapping("/manager/all")
    public ResponseEntity<?> getAllTables(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSeatCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort,
            Authentication authentication) {
        
        log.info("GET /api/tables - status: {}, location: {}, minSeatCount: {}, page: {}, size: {}",
                 status, location, minSeatCount, page, size);

        try {
            // If pagination parameters are provided, use paginated response
            if (page >= 0 || size != 20) {
                // Parse sort parameter
                String[] sortParams = sort.split(",");
                String sortField = sortParams[0];
                Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
                
                Page<TableResponse> tables = service.getTablesWithPagination(
                        status, location, minSeatCount, pageable);
                
                return ResponseEntity.ok(tables);
            } else {
                // Return simple list without pagination
                List<TableResponse> tables = service.getAllTables(status, location, minSeatCount);
                return ResponseEntity.ok(tables);
            }
        } catch (Exception e) {
            log.error("Error fetching tables", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch tables: " + e.getMessage()));
        }
    }

    /**
     * GET /api/tables/{id}
     * Get single table by ID
     *
     * Access: Cashier, Waiter, Manager
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTableById(@PathVariable UUID id, Authentication authentication) {
        log.info("GET /api/tables/{}", id);

        try {
            TableResponse table = service.getTableById(id);
            return ResponseEntity.ok(table);
        } catch (ResourceNotFoundException e) {
            log.error("Table not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching table", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch table: " + e.getMessage()));
        }
    }

    /**
     * POST /api/tables
     * Create a new table
     * 
     * Request Body: CreateTableRequest
     * 
     * Access: Waiter, Manager
     */
    @PostMapping
    public ResponseEntity<?> createTable(
            @Valid @RequestBody CreateTableRequest request,
            Authentication authentication) {
        
        log.info("POST /api/tables - Creating table: {}", request.getName());

        try {
            User currentUser = (User) authentication.getPrincipal();
            TableResponse createdTable = service.createTable(currentUser, request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTable);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized table creation attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating table", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create table: " + e.getMessage()));
        }
    }

    /**
     * POST /api/tables/bulk
     * Bulk create tables
     * 
     * Request Body: List<CreateTableRequest>
     * 
     * Access: Manager
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkCreateTables(
            @Valid @RequestBody List<CreateTableRequest> requests,
            Authentication authentication) {
        
        log.info("POST /api/tables/bulk - Creating {} tables", requests.size());

        try {
            User currentUser = (User) authentication.getPrincipal();
            List<TableResponse> createdTables = service.bulkCreateTables(currentUser, requests);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully created " + createdTables.size() + " tables");
            response.put("tables", createdTables);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized bulk table creation attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error bulk creating tables", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to bulk create tables: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/tables/{id}
     * Update an existing table
     * 
     * Request Body: UpdateTableRequest
     * 
     * Access: Waiter, Manager
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTable(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTableRequest request,
            Authentication authentication) {
        
        log.info("PUT /api/tables/{} - Updating table", id);

        try {
            User currentUser = (User) authentication.getPrincipal();
            TableResponse updatedTable = service.updateTable(currentUser, id, request);
            
            return ResponseEntity.ok(updatedTable);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized table update attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            log.error("Table not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating table", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update table: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/tables/{id}/status
     * Update table status only, as requested by the client.
     * 
     * Query Parameter: status
     * 
     * Access: Waiter, Manager
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTableStatusWithPut(
            @PathVariable UUID id,
            @RequestParam String status,
            Authentication authentication) {
        
        log.info("PUT /api/tables/{}/status?status={}", id, status);

        try {
            if (status == null || status.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Status is required"));
            }

            User currentUser = (User) authentication.getPrincipal();
            TableResponse updatedTable = service.updateTableStatus(currentUser, id, status);
            
            return ResponseEntity.ok(updatedTable);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized table status update attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            log.error("Table not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating table status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update table status: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/tables/{id}/status
     * Update table status only
     * 
     * Request Body: { "status": "Available" }
     * 
     * Access: Waiter, Manager
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTableStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        log.info("PATCH /api/tables/{}/status", id);

        try {
            String status = request.get("status");
            if (status == null || status.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Status is required"));
            }

            User currentUser = (User) authentication.getPrincipal();
            TableResponse updatedTable = service.updateTableStatus(currentUser, id, status);
            
            return ResponseEntity.ok(updatedTable);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized table status update attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            log.error("Table not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating table status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update table status: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/tables/bulk/status
     * Bulk update table status by location
     * 
     * Query Parameter: location
     * Request Body: { "status": "Available" }
     * 
     * Access: Manager
     */
    @PatchMapping("/bulk/status")
    public ResponseEntity<?> bulkUpdateTableStatus(
            @RequestParam String location,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        log.info("PATCH /api/tables/bulk/status - location: {}", location);

        try {
            String status = request.get("status");
            if (status == null || status.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Status is required"));
            }

            User currentUser = (User) authentication.getPrincipal();
            Integer updatedCount = service.bulkUpdateTableStatus(currentUser, location, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully updated " + updatedCount + " tables");
            response.put("updatedCount", updatedCount);
            
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized bulk status update attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error bulk updating table status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to bulk update table status: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/tables/{id}
     * Delete a table
     * 
     * Access: Manager only
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTable(
            @PathVariable UUID id,
            Authentication authentication) {
        
        log.info("DELETE /api/tables/{}", id);

        try {
            User currentUser = (User) authentication.getPrincipal();
            service.deleteTable(currentUser, id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Table deleted successfully");
            response.put("id", id.toString());
            
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized table deletion attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            log.error("Table not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Cannot delete table: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting table", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete table: " + e.getMessage()));
        }
    }

    /**
     * GET /api/tables/available
     * Get available tables by location
     * 
     * Query Parameter: location (optional)
     * 
     * Access: Cashier, Waiter, Manager
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableTables(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSeatCount,
            Authentication authentication) {
        
        log.info("GET /api/tables/available - location: {}, minSeatCount: {}", location, minSeatCount);

        try {
            List<TableResponse> tables;
            
            if (location != null) {
                tables = service.getAvailableTablesByLocation(location);
            } else if (minSeatCount != null) {
                tables = service.getAvailableTablesWithMinSeats(minSeatCount);
            } else {
                tables = service.getAllTables("Available", null, null);
            }
            
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            log.error("Error fetching available tables", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch available tables: " + e.getMessage()));
        }
    }

    /**
     * GET /api/tables/statistics
     * Get table statistics
     * 
     * Access: Manager
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getTableStatistics(Authentication authentication) {
        log.info("GET /api/tables/statistics");

        try {
            User currentUser = (User) authentication.getPrincipal();
            TableStatisticsResponse statistics = service.getTableStatistics(currentUser);
            
            return ResponseEntity.ok(statistics);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized statistics access attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching table statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch table statistics: " + e.getMessage()));
        }
    }

    // ============= Helper Methods =============

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

}
