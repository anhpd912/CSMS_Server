package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.ProductDTO;
import com.fu.coffeeshop_management.server.entity.Product;
import com.fu.coffeeshop_management.server.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> list(String status, String categoryIdStr, String keyword) {
        String s = normalize(status);
        String kw = normalize(keyword);
        UUID catId = parseUUIDOrNull(categoryIdStr);

        List<Product> products = repo.listProducts(s, catId, kw);
        return products.stream().map(ProductDTO::from).toList();
    }

    private static String normalize(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static UUID parseUUIDOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try { return UUID.fromString(s.trim()); }
        catch (IllegalArgumentException ex) { return null; }
    }
}
