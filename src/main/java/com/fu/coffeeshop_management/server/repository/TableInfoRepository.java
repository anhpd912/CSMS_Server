package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.TableInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableInfoRepository extends JpaRepository<TableInfo, UUID> {
    
    /**
     * Finds all tables by their current status (e.g., "Available", "Occupied").
     */
    List<TableInfo> findByStatus(String status);
    
    /**
     * Finds all tables by location
     */
    List<TableInfo> findByLocation(String location);
    
    /**
     * Finds tables by status and location
     */
    List<TableInfo> findByStatusAndLocation(String status, String location);
    
    /**
     * Finds tables with seat count greater than or equal to minimum
     */
    @Query("SELECT t FROM TableInfo t WHERE t.seatCount >= :minSeatCount")
    List<TableInfo> findByMinSeatCount(@Param("minSeatCount") Integer minSeatCount);
    
    /**
     * Finds available tables by location
     */
    @Query("SELECT t FROM TableInfo t WHERE t.status = 'Available' AND t.location = :location")
    List<TableInfo> findAvailableTablesByLocation(@Param("location") String location);
    
    /**
     * Finds available tables with minimum seat count
     */
    @Query("SELECT t FROM TableInfo t WHERE t.status = 'Available' AND t.seatCount >= :minSeatCount")
    List<TableInfo> findAvailableTablesWithMinSeats(@Param("minSeatCount") Integer minSeatCount);
    
    /**
     * Finds table by name
     */
    Optional<TableInfo> findByName(String name);
    
    /**
     * Checks if a table with the given name exists
     */
    boolean existsByName(String name);
    
    /**
     * Counts tables by status
     */
    @Query("SELECT COUNT(t) FROM TableInfo t WHERE t.status = :status")
    Long countByStatus(@Param("status") String status);
    
    /**
     * Counts tables by location
     */
    Long countByLocation(String location);
    
    /**
     * Gets all tables with pagination
     */
    Page<TableInfo> findAll(Pageable pageable);
    
    /**
     * Gets tables by status with pagination
     */
    Page<TableInfo> findByStatus(String status, Pageable pageable);
    
    /**
     * Gets tables by location with pagination
     */
    Page<TableInfo> findByLocation(String location, Pageable pageable);
    
    /**
     * Advanced search with multiple filters
     */
    @Query("SELECT t FROM TableInfo t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:location IS NULL OR t.location = :location) AND " +
           "(:minSeatCount IS NULL OR t.seatCount >= :minSeatCount)")
    Page<TableInfo> searchTables(@Param("status") String status,
                                  @Param("location") String location,
                                  @Param("minSeatCount") Integer minSeatCount,
                                  Pageable pageable);
    
    /**
     * Gets all unique locations
     */
    @Query("SELECT DISTINCT t.location FROM TableInfo t")
    List<String> findAllLocations();
    
    /**
     * Gets average seat count
     */
    @Query("SELECT AVG(t.seatCount) FROM TableInfo t")
    Double getAverageSeatCount();
}
