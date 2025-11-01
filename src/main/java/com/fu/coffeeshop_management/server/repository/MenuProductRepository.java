package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.MenuProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuProductRepository extends JpaRepository<MenuProduct, UUID> {
    List<MenuProduct> findByMenuId(UUID menuId);
}
