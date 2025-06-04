package com.qcommerce.model;

import jakarta.persistence.*;
// import java.util.Set; // Not used if mappedBy is commented out

@Entity
@Table(name = "roles") // Matches your database table name
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed from Integer to Long to match DB 'bigint'

    @Column(nullable = false, unique = true) // Kept entity constraints, DB 'name' is nullable
    private String name; // e.g., "ROLE_USER", "ROLE_ADMIN"

    // If you have a bidirectional relationship from Role to User (optional)
    // @ManyToMany(mappedBy = "roles")
    // private Set<UserEntity> users;


    public RoleEntity() {
    }

    public RoleEntity(String name) {
        this.name = name;
    }
    
    public RoleEntity(Long id, String name) { // Changed id type to Long
        this.id = id;
        this.name = name;
    }


    // Getters and Setters
    public Long getId() { // Changed return type to Long
        return id;
    }

    public void setId(Long id) { // Changed parameter type to Long
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString, equals, hashCode (optional but recommended)
}