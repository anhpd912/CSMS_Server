package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.CategoryDTO;
import com.fu.coffeeshop_management.server.dto.CategoryResponse;
import com.fu.coffeeshop_management.server.entity.Category;
import com.fu.coffeeshop_management.server.repository.CategoryRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) { this.repo = repo; }

    public List<CategoryDTO> listAll() {
        return repo.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    private CategoryDTO toDTO(Category c) {
        String idStr = (c.getId() == null) ? null : c.getId().toString();
        return new CategoryDTO(idStr, c.getName());
    }
}
