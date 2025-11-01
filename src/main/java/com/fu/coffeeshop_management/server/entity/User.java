package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entity representation of the 'user' table.
 * Implements UserDetails for Spring Security integration.
 * Based on the SDD 'user' table definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`user`") // 'user' is a reserved keyword in SQL, so we use backticks
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "fullname", length = 100, nullable = false)
    private String fullname;

    @Column(name = "mobile", length = 20, nullable = false)
    private String mobile;

    // Foreign Key: role_id → role.id
    @ManyToOne(fetch = FetchType.EAGER) // EAGER fetch for roles is usually needed for security
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Foreign Key: loyalty_id → loyalty.loyalty_id (Optional link)
    // We will define this when we build the Loyalty feature.
    // @OneToOne
    // @JoinColumn(name = "loyalty_id")
    // private Loyalty loyalty;


    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We use the Role name (e.g., "MANAGER", "CASHIER") as the authority
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // We use email as the username for login
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
