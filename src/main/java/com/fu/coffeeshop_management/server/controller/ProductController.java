package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.ProductDTO;
import com.fu.coffeeshop_management.server.entity.Product;
import com.fu.coffeeshop_management.server.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // GET /api/products?status=active&categoryId=<uuid>&keyword=cap
    @GetMapping
    public List<ProductDTO> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword
    ) {
        return service.list(status, categoryId, keyword);
    }
}
