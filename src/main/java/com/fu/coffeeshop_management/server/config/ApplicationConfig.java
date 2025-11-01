package com.fu.coffeeshop_management.server.config;

import com.fu.coffeeshop_management.server.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for Spring Security beans.
 * This wires up the services needed for authentication.
 */
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Defines the UserDetailsService bean.
     * This tells Spring Security how to load a user by its username (email).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Defines the AuthenticationProvider bean.
     * This bean is responsible for fetching the user details and encoding passwords.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Defines the AuthenticationManager bean.
     * This is the main component that processes an authentication request.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the PasswordEncoder bean.
     * We use BCrypt for strong, salted password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
