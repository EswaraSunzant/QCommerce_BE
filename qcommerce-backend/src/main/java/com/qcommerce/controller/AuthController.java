package com.qcommerce.controller;

// Import generated API and Models
import com.qcommerce.generated.api.AuthApi;
import com.qcommerce.generated.model.AuthResponse;
import com.qcommerce.generated.model.UserRegistration;
import com.qcommerce.generated.model.LoginRequest; 
import com.qcommerce.generated.model.LogoutUser200Response; // Import the generated response type for logout

// Import custom service
import com.qcommerce.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
// import java.util.Map; // No longer needed for logout response if using a specific model

/**
 * REST Controller for Authentication and User operations.
 * Implements the API contract defined by the generated AuthApi interface.
 */
@RestController
public class AuthController implements AuthApi { 

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles POST requests to /auth/register for registering a new user.
     * This method overrides the one from the generated AuthApi interface.
     *
     * @param userRegistration The user registration data (generated model).
     * @return ResponseEntity containing AuthResponse on successful registration (201 Created).
     */
    @Override
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegistration userRegistration) {
        AuthResponse authResponse = authService.registerUser(userRegistration);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    /**
     * Handles POST requests to /auth/login for logging in an existing user.
     * This method overrides the one from the generated AuthApi interface.
     *
     * @param loginRequest The login credentials (generated model).
     * @return ResponseEntity containing AuthResponse on successful login (200 OK).
     */
    @Override
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(authResponse); 
    }

    /**
     * Handles POST requests to /auth/logout for logging out the current user.
     * This method overrides the one from the generated AuthApi interface.
     *
     * @return ResponseEntity containing LogoutUser200Response on successful logout.
     */
    @Override
    public ResponseEntity<LogoutUser200Response> logoutUser() {
        authService.logoutUser();
        // Create an instance of the generated LogoutUser200Response model
        LogoutUser200Response response = new LogoutUser200Response();
        response.setMessage("Logout successful"); // Assuming the generated model has a setMessage method
        return ResponseEntity.ok(response);
    }
}
