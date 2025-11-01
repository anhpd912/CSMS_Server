package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Order;
import com.fu.coffeeshop_management.server.entity.TableInfo;
import com.fu.coffeeshop_management.server.entity.TableOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableOrderRepository extends JpaRepository<TableOrder, UUID> {
    List<TableOrder> findByOrder(Order order);
    List<TableOrder> findByTableInfo(TableInfo tableInfo);
}
