package com.qcommerce.user;

import com.qcommerce.security.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING) // Store roles as strings
    private Set<Role> roles;

    private Boolean isActive = true;
    private String locale;

    // Address Book
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_addresses", joinColumns = @JoinColumn(name = "user_id"))
    private List<Address> addresses = new ArrayList<>();
}