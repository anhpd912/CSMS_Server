package com.fu.coffeeshop_management.server.dto;

import com.fu.coffeeshop_management.server.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageLink;
    private String status;
    private UUID categoryId;
    private String categoryName;


    public ProductDTO() {
    }

    public ProductDTO(UUID id, String name, String description,
                      BigDecimal price, String imageLink, String status,
                      UUID categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageLink = imageLink;
        this.status = status;
        this.categoryId = categoryId;
    }

    public static ProductDTO from(Product p) {
        ProductDTO d = new ProductDTO();
        d.id = p.getId();
        d.name = p.getName();
        d.description = p.getDescription();
        d.price = p.getPrice();
        d.imageLink = p.getImageLink();
        d.status = p.getStatus();
        if (p.getCategory() != null) {
            d.categoryId = p.getCategory().getId();
            d.categoryName = p.getCategory().getName();
        }
        return d;
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
