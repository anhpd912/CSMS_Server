package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.TableInfo;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.TableInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class TableInfoService {
    private final TableInfoRepository tableRepo;

    private static final String STATUS_AVAILABLE = "Available";
    private static final String STATUS_OCCUPIED = "Occupied";
    private static final String STATUS_RESERVED = "Reserved";

    // Location constants
    private static final String LOCATION_INDOOR = "Indoor";
    private static final String LOCATION_OUTDOOR = "Outdoor";
    private static final String LOCATION_BALCONY = "Balcony";

    // Role constants
    private static final String ROLE_CASHIER = "CASHIER";
    private static final String ROLE_WAITER = "WAITER";
    private static final String ROLE_MANAGER = "MANAGER";

    // Helper method to convert TableInfo entity to TableInfoDTO
    private TableInfoDTO convertToDTO(TableInfo tableInfo) {
        return TableInfoDTO.builder()
                .id(tableInfo.getId())
                .name(tableInfo.getName())
                .location(tableInfo.getLocation())
                .status(tableInfo.getStatus())
                .seatCount(tableInfo.getSeatCount())
                .build();
    }

    public List<TableInfoDTO> listTables(String status, String keyword) {

        boolean hasStatus = (status != null && !status.isBlank());
        boolean hasKeyword = (keyword != null && !keyword.isBlank());

        List<TableInfo> tables;
        if (hasStatus && hasKeyword) {
            tables = tableRepo.findByStatusIgnoreCaseAndNameContainingIgnoreCase(status, keyword);
        }
        else if (hasStatus) {
            tables = tableRepo.findByStatusIgnoreCase(status);
        }
        else if (hasKeyword) {
            tables = tableRepo.findByNameContainingIgnoreCase(keyword);
        }
        else {
            tables = tableRepo.findAll();
        }
        return tables.stream().map(this::mapToTableInfoDTO).collect(Collectors.toList());
    }

    /**
     * Get all tables with optional filtering
     * Accessible by: Cashier, Waiter, Manager
     */
    @Transactional(readOnly = true)
    public List<TableResponse> getAllTables(String status, String location, Integer minSeatCount) {
        log.info("Fetching all tables with filters - status: {}, location: {}, minSeatCount: {}",
                 status, location, minSeatCount);

        List<TableInfo> tables;

        // Apply filters based on provided parameters
        if (status != null && location != null) {
            tables = tableRepo.findByStatusAndLocation(status, location);
        } else if (status != null) {
            tables = tableRepo.findByStatus(status);
        } else if (location != null) {
            tables = tableRepo.findByLocation(location);
        } else if (minSeatCount != null) {
            tables = tableRepo.findByMinSeatCount(minSeatCount);
        } else {
            tables = tableRepo.findAll();
        }

        // Apply additional filtering for minSeatCount if needed
        if (minSeatCount != null && (status != null || location != null)) {
            tables = tables.stream()
                    .filter(t -> t.getSeatCount() >= minSeatCount)
                    .collect(Collectors.toList());
        }

        return tables.stream()
                .map(this::mapToTableResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get tables with pagination
     * Accessible by: Cashier, Waiter, Manager
     */
    @Transactional(readOnly = true)
    public Page<TableResponse> getTablesWithPagination(String status, String location,
                                                        Integer minSeatCount, Pageable pageable) {
        log.info("Fetching tables with pagination - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        Page<TableInfo> tables = tableRepo.searchTables(status, location, minSeatCount, pageable);

        return tables.map(this::mapToTableResponse);
    }

    /**
     * Get single table by ID
     * Accessible by: Cashier, Waiter, Manager
     */
    @Transactional(readOnly = true)
    public TableResponse getTableById(UUID tableId) {
        log.info("Fetching table by ID: {}", tableId);

        TableInfo table = tableRepo.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));

        return mapToTableResponse(table);
    }

    /**
     * Create a new table
     * Accessible by: Waiter, Manager
     */
    @Transactional
    public TableResponse createTable(User currentUser, CreateTableRequest request) {
        log.info("Creating new table - name: {}, user: {}", request.getName(), currentUser.getUsername());

        // Authorization check - only Waiter and Manager can create tables
        validateCreatePermission(currentUser);

        // Validate unique table name
        if (tableRepo.existsByName(request.getName())) {
            throw new IllegalArgumentException("Table with name '" + request.getName() + "' already exists");
        }

        // Create table entity
        TableInfo table = TableInfo.builder()
                .name(request.getName())
                .location(request.getLocation())
                .seatCount(request.getSeatCount())
                .status(request.getStatus() != null ? request.getStatus() : STATUS_AVAILABLE)
                .build();

        TableInfo savedTable = tableRepo.save(table);
        log.info("Table created successfully with ID: {}", savedTable.getId());

        return mapToTableResponse(savedTable);
    }

    /**
     * Bulk create tables
     * Accessible by: Manager
     */
    @Transactional
    public List<TableResponse> bulkCreateTables(User currentUser, List<CreateTableRequest> requests) {
        log.info("Bulk creating {} tables", requests.size());

        // Authorization check - only Manager can bulk create
        validateManagerPermission(currentUser);

        List<TableInfo> tables = new ArrayList<>();

        for (CreateTableRequest request : requests) {
            // Validate unique table name
            if (tableRepo.existsByName(request.getName())) {
                log.warn("Skipping table '{}' - already exists", request.getName());
                continue;
            }

            TableInfo table = TableInfo.builder()
                    .name(request.getName())
                    .location(request.getLocation())
                    .seatCount(request.getSeatCount())
                    .status(request.getStatus() != null ? request.getStatus() : STATUS_AVAILABLE)
                    .build();

            tables.add(table);
        }

        List<TableInfo> savedTables = tableRepo.saveAll(tables);
        log.info("Successfully created {} tables", savedTables.size());

        return savedTables.stream()
                .map(this::mapToTableResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing table
     * Accessible by: Waiter, Manager
     */
    @Transactional
    public TableResponse updateTable(User currentUser, UUID tableId, UpdateTableRequest request) {
        log.info("Updating table ID: {} by user: {}", tableId, currentUser.getUsername());

        // Authorization check - only Waiter and Manager can update tables
        validateUpdatePermission(currentUser);

        // Find existing table
        TableInfo table = tableRepo.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));

        // Check if name is being changed and if new name already exists
        if (!table.getName().equals(request.getName()) &&
            tableRepo.existsByName(request.getName())) {
            throw new IllegalArgumentException("Table with name '" + request.getName() + "' already exists");
        }

        // Update table fields
        table.setName(request.getName());
        table.setLocation(request.getLocation());
        table.setSeatCount(request.getSeatCount());
        table.setStatus(request.getStatus());

        TableInfo updatedTable = tableRepo.save(table);
        log.info("Table updated successfully: {}", updatedTable.getId());

        return mapToTableResponse(updatedTable);
    }

    /**
     * Update table status only
     * Accessible by: Waiter, Manager
     */
    @Transactional
    public TableResponse updateTableStatus(User currentUser, UUID tableId, String status) {
        log.info("Updating table status - ID: {}, new status: {}", tableId, status);

        // Authorization check
        validateUpdatePermission(currentUser);

        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        TableInfo table = tableRepo.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));

        table.setStatus(status);
        TableInfo updatedTable = tableRepo.save(table);

        log.info("Table status updated successfully: {}", updatedTable.getId());
        return mapToTableResponse(updatedTable);
    }

    /**
     * Bulk update table status by location
     * Accessible by: Manager
     */
    @Transactional
    public Integer bulkUpdateTableStatus(User currentUser, String location, String status) {
        log.info("Bulk updating table status - location: {}, status: {}", location, status);

        // Authorization check
        validateManagerPermission(currentUser);

        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        List<TableInfo> tables = tableRepo.findByLocation(location);

        for (TableInfo table : tables) {
            table.setStatus(status);
        }

        tableRepo.saveAll(tables);
        log.info("Successfully updated {} tables", tables.size());

        return tables.size();
    }

    /**
     * Delete a table
     * Accessible by: Manager only
     */
    @Transactional
    public void deleteTable(User currentUser, UUID tableId) {
        log.info("Deleting table ID: {} by user: {}", tableId, currentUser.getUsername());

        // Authorization check - only Manager can delete tables
        validateDeletePermission(currentUser);

        // Check if table exists
        TableInfo table = tableRepo.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));

        // Check if table is occupied (business rule: cannot delete occupied tables)
        if (STATUS_OCCUPIED.equals(table.getStatus())) {
            throw new IllegalStateException("Cannot delete occupied table. Please clear the table first.");
        }

        tableRepo.delete(table);
        log.info("Table deleted successfully: {}", tableId);
    }

    /**
     * Get available tables by location
     * Accessible by: Cashier, Waiter, Manager
     */
    @Transactional(readOnly = true)
    public List<TableResponse> getAvailableTablesByLocation(String location) {
        log.info("Fetching available tables for location: {}", location);

        List<TableInfo> tables = tableRepo.findAvailableTablesByLocation(location);

        return tables.stream()
                .map(this::mapToTableResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get available tables with minimum seat count
     * Accessible by: Cashier, Waiter, Manager
     */
    @Transactional(readOnly = true)
    public List<TableResponse> getAvailableTablesWithMinSeats(Integer minSeatCount) {
        log.info("Fetching available tables with minimum {} seats", minSeatCount);

        List<TableInfo> tables = tableRepo.findAvailableTablesWithMinSeats(minSeatCount);

        return tables.stream()
                .map(this::mapToTableResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get table statistics
     * Accessible by: Manager
     */
    @Transactional(readOnly = true)
    public TableStatisticsResponse getTableStatistics(User currentUser) {
        log.info("Fetching table statistics");

        // Authorization check
        validateManagerPermission(currentUser);

        Long totalTables = tableRepo.count();
        Long availableTables = tableRepo.countByStatus(STATUS_AVAILABLE);
        Long occupiedTables = tableRepo.countByStatus(STATUS_OCCUPIED);
        Long reservedTables = tableRepo.countByStatus(STATUS_RESERVED);

        // Statistics by location
        Map<String, Integer> tablesByLocation = new HashMap<>();
        List<String> locations = tableRepo.findAllLocations();
        for (String location : locations) {
            tablesByLocation.put(location, Math.toIntExact(tableRepo.countByLocation(location)));
        }

        // Statistics by capacity
        Map<String, Integer> tablesByCapacity = new HashMap<>();
        List<TableInfo> allTables = tableRepo.findAll();
        tablesByCapacity.put("1-2 seats", (int) allTables.stream().filter(t -> t.getSeatCount() <= 2).count());
        tablesByCapacity.put("3-4 seats", (int) allTables.stream().filter(t -> t.getSeatCount() >= 3 && t.getSeatCount() <= 4).count());
        tablesByCapacity.put("5-6 seats", (int) allTables.stream().filter(t -> t.getSeatCount() >= 5 && t.getSeatCount() <= 6).count());
        tablesByCapacity.put("7+ seats", (int) allTables.stream().filter(t -> t.getSeatCount() >= 7).count());

        // Calculate occupancy rate
        Double occupancyRate = totalTables > 0 ?
                (occupiedTables.doubleValue() / totalTables.doubleValue()) * 100 : 0.0;

        // Calculate average seats per table
        Double averageSeatsPerTable = tableRepo.getAverageSeatCount();

        return TableStatisticsResponse.builder()
                .totalTables(Math.toIntExact(totalTables))
                .availableTables(Math.toIntExact(availableTables))
                .occupiedTables(Math.toIntExact(occupiedTables))
                .reservedTables(Math.toIntExact(reservedTables))
                .tablesByLocation(tablesByLocation)
                .tablesByCapacity(tablesByCapacity)
                .occupancyRate(occupancyRate)
                .averageSeatsPerTable(averageSeatsPerTable != null ? averageSeatsPerTable : 0.0)
                .build();
    }

    // ============= Authorization Helper Methods =============

    private void validateCreatePermission(User user) {
        String role = user.getRole().getName();
        if (!ROLE_WAITER.equals(role) && !ROLE_MANAGER.equals(role)) {
            throw new UnauthorizedException("Only Waiters and Managers can create tables");
        }
    }

    private void validateUpdatePermission(User user) {
        String role = user.getRole().getName();
        if (!ROLE_WAITER.equals(role) && !ROLE_MANAGER.equals(role)) {
            throw new UnauthorizedException("Only Waiters and Managers can update tables");
        }
    }

    private void validateDeletePermission(User user) {
        String role = user.getRole().getName();
        if (!ROLE_MANAGER.equals(role)) {
            throw new UnauthorizedException("Only Managers can delete tables");
        }
    }

    private void validateManagerPermission(User user) {
        String role = user.getRole().getName();
        if (!ROLE_MANAGER.equals(role)) {
            throw new UnauthorizedException("Only Managers can access this resource");
        }
    }

    // ============= Validation Helper Methods =============

    private boolean isValidStatus(String status) {
        return STATUS_AVAILABLE.equals(status) ||
               STATUS_OCCUPIED.equals(status) ||
               STATUS_RESERVED.equals(status);
    }

    // ============= Mapping Helper Methods =============

    private TableResponse mapToTableResponse(TableInfo table) {
        return TableResponse.builder()
                .id(table.getId())
                .name(table.getName())
                .location(table.getLocation())
                .seatCount(table.getSeatCount())
                .status(table.getStatus())
                .isAvailable(STATUS_AVAILABLE.equals(table.getStatus()))
                .currentOrders(0) // TODO: Calculate from actual orders when Order feature is implemented
                .build();
    }

    // ============= Custom Exception Classes =============

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

}
