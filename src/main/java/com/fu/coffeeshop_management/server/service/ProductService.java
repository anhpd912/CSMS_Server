package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.ProductCreateRequest;
import com.fu.coffeeshop_management.server.dto.ProductDTO;
import com.fu.coffeeshop_management.server.dto.ProductResponse;
import com.fu.coffeeshop_management.server.dto.ProductUpdateRequest;
import com.fu.coffeeshop_management.server.entity.Category;
import com.fu.coffeeshop_management.server.entity.Product;
import com.fu.coffeeshop_management.server.repository.CategoryRepository;
import com.fu.coffeeshop_management.server.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing products.
 * This class handles the business logic related to products, including creation, update, and retrieval.
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves a list of products with optional filters for category and status.
     *
     * @param categoryName The name of the category to filter by (optional).
     * @param status       The status of the products to filter by (optional).
     * @return A list of {@link ProductResponse} objects matching the filters.
     */
    public List<ProductResponse> getAllWithFilters(String categoryName, String keyword) {
        List<Product> products;
        if (categoryName != null && keyword != null) {
            products = productRepository.findByCategoryNameAndKeyword(categoryName, keyword);
        } else if (categoryName != null) {
            products = productRepository.findByCategoryId(categoryRepository.findByName(categoryName).get().getId());
        } else if (keyword != null) {
            products = productRepository.findByKeyword(keyword);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new product.
     *
     * @param productCreateRequest The request object containing the product details.
     * @param imageUrl             The URL of the uploaded image for the product.
     * @return A {@link ProductResponse} object for the newly created product.
     * @throws RuntimeException if the category is not found.
     */
    public ProductResponse createProduct(ProductCreateRequest productCreateRequest, String imageUrl) {
        Category category = categoryRepository.findByName(productCreateRequest.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product productEntity = Product.builder()
                .name(productCreateRequest.getName())
                .description(productCreateRequest.getDescription())
                .price(productCreateRequest.getPrice())
                .imageLink(imageUrl)
                .category(category)
                .build();
        productRepository.save(productEntity);
        return mapToProductResponse(productEntity);
    }

    /**
     * Updates an existing product.
     *
     * @param productId            The ID of the product to update.
     * @param productUpdateRequest The request object containing the updated product details.
     * @param newImageUrl          The new image URL for the product (optional). If null, the existing image is preserved.
     * @return A {@link ProductResponse} object for the updated product.
     * @throws RuntimeException if the product or category is not found.
     */
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest productUpdateRequest, String newImageUrl) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setName(productUpdateRequest.getName());
        existingProduct.setDescription(productUpdateRequest.getDescription());
        existingProduct.setPrice(productUpdateRequest.getPrice());
        existingProduct.setStatus(productUpdateRequest.getStatus());

        if (productUpdateRequest.getCategoryName() != null) {
            Category category = categoryRepository.findByName(productUpdateRequest.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingProduct.setCategory(category);
        }

        if (newImageUrl != null) {
            existingProduct.setImageLink(newImageUrl);
        }

        productRepository.save(existingProduct);
        return mapToProductResponse(existingProduct);
    }

    /**
     * Maps a {@link Product} entity to a {@link ProductResponse} DTO.
     *
     * @param product The product entity to map.
     * @return The mapped {@link ProductResponse} DTO.
     */
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageLink(product.getImageLink())
                .status(product.getStatus())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public ProductResponse getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToProductResponse(product);
    }

    public void updateStatusProduct(UUID productId, Boolean status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(status ? "active" : "inactive");
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> list(String status, String categoryIdStr, String keyword) {
        String s = normalize(status);
        String kw = normalize(keyword);
        UUID catId = parseUUIDOrNull(categoryIdStr);

        List<Product> products = productRepository.listProducts(s, catId, kw);
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
