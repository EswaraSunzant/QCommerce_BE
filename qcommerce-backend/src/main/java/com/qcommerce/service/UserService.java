package com.qcommerce.service;

// Import generated Models
import com.qcommerce.generated.model.Role;
import com.qcommerce.generated.model.User;
import com.qcommerce.generated.model.UserRegistration;
import com.qcommerce.generated.model.Address; 
import com.qcommerce.generated.model.RoleReference;


import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong; // Changed to AtomicLong for IDs
import java.util.stream.Collectors;

/**
 * Service for managing users (in-memory implementation).
 */
@Service
public class UserService {

    // Use email as key for users map for easier lookup
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    // Use Long as key for roles map to match generated ID type
    private final Map<Long, Role> rolesById = new ConcurrentHashMap<>(); 
    private final AtomicLong userIdCounter = new AtomicLong(0);
    private final AtomicLong roleIdCounter = new AtomicLong(0);

    public UserService() {
        // Initialize some default roles
        createRole("ROLE_USER");
        createRole("ROLE_ADMIN");
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setId(roleIdCounter.incrementAndGet()); // ID is Long
        role.setName(name);
        rolesById.put(role.getId(), role);
        return role;
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }

    // This method is for saving to the in-memory store, 
    // distinct from AuthService which saves to DB.
    // It assumes UserRegistration and User are generated models.
    public User saveUser(UserRegistration registration, String encodedPassword) {
        // Note: encodedPassword is not used in this in-memory example for User model,
        // as the generated User model typically doesn't store the password.
        // If it did, you'd set it here.

        User newUser = new User();
        newUser.setId(userIdCounter.incrementAndGet()); // ID is Long
        newUser.setEmail(registration.getEmail().toLowerCase());
        
        if (registration.getPhone().isPresent()) {
            newUser.setPhone(registration.getPhone()); // setPhone expects JsonNullable<String>
        } else {
            newUser.setPhone(JsonNullable.undefined());
        }

        if (registration.getLocale().isPresent() && registration.getLocale().get() != null) {
            // User.setLocale expects JsonNullable<String>
            // UserRegistration.LocaleEnum.getValue() returns String
            newUser.setLocale(JsonNullable.of(registration.getLocale().get().getValue()));
        } else {
            newUser.setLocale(JsonNullable.undefined());
        }
        
        newUser.setIsActive(true); // Default new users to active

        // Initialize addresses if the field exists in User model (as per OpenAPI spec)
        // Assuming User.setAddresses expects JsonNullable<List<Address>>
        newUser.setAddresses(JsonNullable.of(new ArrayList<>()));


        // Handle roles
        if (registration.getRoles().isPresent() && registration.getRoles().get() != null && !registration.getRoles().get().isEmpty()) {
            List<RoleReference> roleReferences = registration.getRoles().get();
            List<Role> assignedUserRoles = roleReferences.stream()
                .map(roleRef -> rolesById.get(roleRef.getId())) // RoleReference.getId() is Long
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
            newUser.setRoles(JsonNullable.of(assignedUserRoles));
        } else {
            // Assign a default role (e.g., ROLE_USER) if none provided
            Role defaultRole = rolesById.values().stream()
                .filter(r -> "ROLE_USER".equals(r.getName()))
                .findFirst()
                .orElse(null); // Or create if not exists, or throw error

            if (defaultRole != null) {
                newUser.setRoles(JsonNullable.of(List.of(defaultRole)));
            } else {
                 newUser.setRoles(JsonNullable.of(new ArrayList<>())); // Empty list if no default
            }
        }
        
        usersByEmail.put(newUser.getEmail(), newUser);
        return newUser;
    }

    public Optional<Role> findRoleById(Long id) { // Parameter type changed to Long
        return Optional.ofNullable(rolesById.get(id));
    }
}
