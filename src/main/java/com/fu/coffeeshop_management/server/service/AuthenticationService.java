package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.*;
import com.fu.coffeeshop_management.server.entity.Role;
import com.fu.coffeeshop_management.server.entity.User;
import com.fu.coffeeshop_management.server.repository.RoleRepository;
import com.fu.coffeeshop_management.server.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    private final JavaMailSender mailSender;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            JavaMailSender mailSender
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid email or password");
        }


        // If authentication was successful, find the user and generate a token
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .isSuccess(true)
                .errorCode("200")
                .message("Login successful")
                .token(jwtToken)
                .role(user.getRole().getName())
                .build();
    }

    // --- Stubbed Methods from SDD ---

    public UserResponse changePassword(UpdatePassWordRequest request) {

        var user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.get().getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user.get());
        return UserResponse.builder()
                .id(user.get().getId())
                .email(user.get().getEmail())
                .fullname(user.get().getFullname())
                .mobile(user.get().getMobile())
                .role(user.get().getRole().getName())
                .build();
    }

    public void resetPassword(String email) {
        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Password Reset</title>
                      <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px;
                                     box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                        .password-box { background-color: #f0f0f0; border: 1px solid #dddddd; padding: 15px; text-align: center;
                                        font-size: 20px; font-weight: bold; letter-spacing: 2px; border-radius: 4px; margin: 20px 0; }
                      </style>
                    </head>
                    <body>
                      <div class="container">
                        <h1>Password Reset</h1>
                        <p>Hello,</p>
                        <p>You requested a password reset. Your new temporary password is:</p>
                        <div class="password-box">{{PASSWORD}}</div>
                        <p>Please change this password after logging in for security reasons.</p>
                        <p>If you did not request a password reset, please ignore this email.</p>
                        <div class="footer"><p>&copy; 2024 Coffeeshop Management. All rights reserved.</p></div>
                      </div>
                    </body>
                    </html>
                    """;

            htmlMsg = htmlMsg.replace("{{PASSWORD}}", newPassword);
            helper.setText(htmlMsg, true);

            helper.setTo(email);
            helper.setSubject("Your New Password");
            mailSender.send(mimeMessage);

        } catch (MailException | MessagingException e) {
            // Log the exception or handle it appropriately
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

}
