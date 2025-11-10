package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.UserRequest;
import com.fu.coffeeshop_management.server.dto.UserResponse;
import com.fu.coffeeshop_management.server.dto.UserResponseDTO;
import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import com.fu.coffeeshop_management.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Helper method to convert User entity to UserResponse DTO
    private UserResponse convertToDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .mobile(user.getMobile())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + userRequest.getRoleId()));

        User user = User.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .fullname(userRequest.getFullname())
                .mobile(userRequest.getMobile())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .mobile(user.getMobile())
                .roleName(user.getRole() != null ? user.getRole().getName() : "N/A")
                .build();
    }
}
