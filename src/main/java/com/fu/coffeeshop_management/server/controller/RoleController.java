package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.RoleResponse;
import com.fu.coffeeshop_management.server.service.RoleService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponse> listAllRoles() {
        return roleService.findAllRoles();
    }
}
