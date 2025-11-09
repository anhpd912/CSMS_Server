package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.TableInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableInfoRepository extends JpaRepository<TableInfo, UUID> {
    /**
     * Finds all tables by their current status (e.g., "Available", "Occupied").
     */
    List<TableInfo> findByStatus(String status);

    List<TableInfo> findByStatusIgnoreCase(String status);

    List<TableInfo> findByNameContainingIgnoreCase(String name);

    List<TableInfo> findByStatusIgnoreCaseAndNameContainingIgnoreCase(String status, String name);

}