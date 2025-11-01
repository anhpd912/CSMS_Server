package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.AuthenticationRequest;
import com.fu.coffeeshop_management.server.dto.AuthenticationResponse;
import com.fu.coffeeshop_management.server.dto.RegisterRequest;
import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import com.fu.coffeeshop_management.server.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class implementing the business logic for authentication.
 * Based on the SDD 'AuthenticationService' class specification.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Implements UC-0103 "Create account".
     * Registers a new user.
     *
     * @param request The registration request DTO.
     * @return An AuthenticationResponse with a new JWT.
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Find the "WAITER" role or create it if it doesn't exist
        // In a real app, you'd have a seed data script for roles
        Role userRole = roleRepository.findByName("WAITER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("WAITER").build()));

        var user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .role(userRole)
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Implements UC-0101 "Login".
     * Authenticates a user.
     *
     * @param request The authentication request DTO.
     * @return An AuthenticationResponse with a new JWT.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // This will trigger the authentication provider to check the password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If authentication was successful, find the user and generate a token
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    
    // --- Stubbed Methods from SDD ---
    
    // public void changePassword(String username, String oldPwd, String newPwd) {
    //     // TODO: Implement change password logic
    //     // 1. Find user by username
    //     // 2. Check if oldPwd matches
    //     // 3. Encode and save newPwd
    // }

    // public void resetPassword(String email) {
    //     // TODO: Implement reset password logic
    //     // 1. Find user by email
    //     // 2. Generate a reset token (and save it with an expiry)
    //     // 3. Call MailService to send the email
    // }
}
