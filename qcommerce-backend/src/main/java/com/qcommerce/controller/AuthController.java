package com.qcommerce.controller;

// Import generated API and Models from the package structure (without Dto suffix)
import com.qcommerce.generated.api.AuthApi;
import com.qcommerce.generated.model.AuthResponse;
import com.qcommerce.generated.model.UserRegistration;

// Import custom service from the package structure
import com.qcommerce.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * REST Controller for Authentication and User operations.
 * Implements the API contract defined by the generated AuthApi interface.
 */
@RestController
public class AuthController implements AuthApi { // Implement the generated interface

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles POST requests to /auth/register for registering a new user.
     * This method overrides the one from the generated AuthApi interface.
     * The @Valid annotation for request body validation will be on the interface method.
     * The @RequestBody annotation will also be on the interface method.
     *
     * @param userRegistration The user registration data (generated model).
     * @return ResponseEntity containing AuthResponse on successful registration (201 Created).
     */
    @Override
    public ResponseEntity<AuthResponse> registerUser(@Valid UserRegistration userRegistration) {
        // The @Valid on the parameter comes from the generated AuthApi interface method.
        // If validation (triggered by @Valid) fails, MethodArgumentNotValidException will be thrown,
        // which is handled by GlobalExceptionHandler.

        AuthResponse authResponse = authService.registerUser(userRegistration);

        // Return 201 Created status with the AuthResponse in the body
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    // Implement other methods from AuthApi if they exist in your OpenAPI specification.
    // For example, if your OpenAPI spec had a /auth/login endpoint,
    // there would be a loginUser(...) method in AuthApi to override here.
}
