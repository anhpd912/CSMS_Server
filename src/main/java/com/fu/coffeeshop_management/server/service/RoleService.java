package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.RoleResponse;
import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Helper method to convert Role entity to RoleResponse DTO
    private RoleResponse convertToDTO(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
