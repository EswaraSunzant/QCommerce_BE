package com.qcommerce.user;

import com.qcommerce.security.Role; // Make sure this import is correct
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority; // Import this
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import this
import org.springframework.security.core.userdetails.UserDetails; // Import this

import java.util.ArrayList;
import java.util.Collection; // Import this
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors; // Import this

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// >>>>>> Implement UserDetails interface <<<<<<
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String passwordHash; // You store the hashed password here

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = {@JoinColumn(name = "user_id")}) // <-- Added {}
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private Boolean isActive = true; // Represents if the account is enabled
    private String locale;

    // Address Book
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_addresses", joinColumns = {@JoinColumn(name = "user_id")}) // <-- Added {}
    private List<Address> addresses = new ArrayList<>();

    // >>>>>> Implement methods from UserDetails <<<<<<

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This method should return the user's roles or authorities.
        // Since your 'roles' field is a Set<Role> and your Role enum/class
        // likely implements GrantedAuthority (or can be converted),
        // you can return that collection.
        if (this.roles == null) {
            return Collections.emptyList(); // Return empty list if no roles
        }
        // Assuming your 'Role' enum/class implements GrantedAuthority:
        return new ArrayList<>(this.roles);

        // If Role does NOT implement GrantedAuthority but has a getName() method:
        // return this.roles.stream()
        //         .map(role -> new SimpleGrantedAuthority(role.getName()))
        //         .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        // Return the field containing the user's password hash
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        // Return the field used as the unique identifier for authentication.
        // Based on your UserRepository and common practice, this is often the email.
        return this.email;
        // If you intend to authenticate by phone, return this.phone;
        // If you have a dedicated username field, return that field.
    }

    @Override
    public boolean isAccountNonExpired() {
        // Return true if the user account has not expired.
        // Implement your logic if accounts can expire.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Return true if the user account is not locked.
        // Implement your logic if accounts can be locked.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Return true if the user's credentials (password) have not expired.
        // Implement your logic if passwords expire.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Return the status of the user account (e.g., based on the isActive field)
        return this.isActive != null ? this.isActive : false; // Use isActive field
    }

    // You might need to add an import for java.util.Collections if you use Collections.emptyList()
    // Ensure you also have necessary imports for GrantedAuthority and SimpleGrantedAuthority if needed.
}