package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.UserRequest;
import com.fu.coffeeshop_management.server.dto.UserResponse;
import com.fu.coffeeshop_management.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> listAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse newUser = userService.createUser(userRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
