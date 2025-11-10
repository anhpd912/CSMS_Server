package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.CategoryDTO;
import com.fu.coffeeshop_management.server.service.CategoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping
    public List<CategoryDTO> list() {
        return service.listAll();
    }
}
