package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.CategoryResponse;
import com.fu.coffeeshop_management.server.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(category -> new CategoryResponse(category.getName(), category.getDescription())).toList();
    }
}
