package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.UserRequest;
import com.fu.coffeeshop_management.server.dto.UserResponse;
import com.fu.coffeeshop_management.server.dto.UserResponseDTO;
import com.fu.coffeeshop_management.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/manage")
    public List<UserResponse> listAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/manage")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse newUser = userService.createUser(userRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
