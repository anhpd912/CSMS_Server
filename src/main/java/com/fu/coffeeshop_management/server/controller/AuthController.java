package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication endpoints.
 * Based on the SDD 'AuthController' class specification.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Handles the /api/auth/register endpoint.
     * Implements UC-0103 "Create account".
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Handles the /api/auth/login endpoint.
     * Implements UC-0101 "Login".
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Handles the /api/auth/me endpoint.
     * This corresponds to the 'me(session)' method in your SDD.
     * It retrieves the currently authenticated user's details.
     *
     * @param authentication The Spring Security authentication principal.
     * @return A DTO with the user's information.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        // Spring Security injects the 'Authentication' object for an authenticated user.
        // We can safely cast the Principal to our User entity.
        User currentUser = (User) authentication.getPrincipal();

        UserResponse response = UserResponse.builder().id(currentUser.getId()).email(currentUser.getEmail()).fullname(currentUser.getFullname()).mobile(currentUser.getMobile()).role(currentUser.getRole().getName()).build();

        return ResponseEntity.ok(response);
    }

    // Note: The /logout endpoint is handled by the SecurityConfig filter chain.
    @PutMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(@RequestBody @Valid UpdatePassWordRequest request) {
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request.getEmail());
        return ResponseEntity.ok(APIResponse.<String>builder().isSuccess(true).message("Password reset email sent successfully.").build());
    }
}
