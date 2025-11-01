package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.PurchaseOrder;
import com.fu.coffeeshop_management.server.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, UUID> {
    List<PurchaseOrderDetail> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
