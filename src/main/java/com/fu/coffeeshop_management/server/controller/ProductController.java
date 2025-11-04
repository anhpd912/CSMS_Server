package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.ProductCreateRequest;
import com.fu.coffeeshop_management.server.dto.ProductResponse;
import com.fu.coffeeshop_management.server.dto.ProductUpdateRequest;
import com.fu.coffeeshop_management.server.service.CloudinaryService;
import com.fu.coffeeshop_management.server.service.ProductService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;

    public ProductController(ProductService productService, CloudinaryService cloudinaryService) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam @Nullable String category, @Nullable @RequestParam String status) {
        return ResponseEntity.ok(productService.getAllWithFilters(category, status));
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> createProduct(@RequestPart("product") @Valid ProductCreateRequest product, @RequestPart("image") MultipartFile image) {
        String imageUrl = cloudinaryService.uploadFile(image);
        return ResponseEntity.ok(productService.createProduct(product, imageUrl));
    }

    @GetMapping("/view/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @RequestPart("product") @Valid ProductUpdateRequest product,
            @RequestPart(name = "image", required = false) MultipartFile image) {

        String newImageUrl = null;
        if (image != null && !image.isEmpty()) {
            newImageUrl = cloudinaryService.uploadFile(image);
        }

        return ResponseEntity.ok(productService.updateProduct(productId, product, newImageUrl));
    }
}
