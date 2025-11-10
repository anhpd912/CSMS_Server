package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.*;
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
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;

    public ProductController(ProductService productService, CloudinaryService cloudinaryService) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public List<ProductDTO> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword
    ) {
        return productService.list(status, categoryId, keyword);
    }

    // GET /api/products?status=active&categoryId=<uuid>&keyword=cap
    @GetMapping("/manage")
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam @Nullable String category, @RequestParam @Nullable String keyword) {
        return ResponseEntity.ok(productService.getAllWithFilters(category, keyword));
    }

    @PostMapping("/manage/add")
    public ResponseEntity<ProductResponse> createProduct(@RequestPart("product") @Valid ProductCreateRequest product, @RequestPart("image") MultipartFile image) {
        String imageUrl = cloudinaryService.uploadFile(image);
        return ResponseEntity.ok(productService.createProduct(product, imageUrl));
    }

    @GetMapping("/manage/view/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PutMapping("/manage/update/{productId}")
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

    @PutMapping("/manage/update-status/{productId}")
    public ResponseEntity<APIResponse> updateStatusProduct(@PathVariable UUID productId, @RequestParam Boolean status) {
        productService.updateStatusProduct(productId, status);
        return ResponseEntity.ok(APIResponse.builder().isSuccess(true).message("Status updated successfully").build());
    }
}
