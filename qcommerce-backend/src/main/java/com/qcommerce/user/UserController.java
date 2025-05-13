package com.qcommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return userService.logoutUser();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody PasswordResetRequest request) {
        return userService.resetPasswordRequest(request);
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> resetPasswordConfirm(@RequestBody PasswordResetConfirmRequest request) {
        return userService.resetPasswordConfirm(request);
    }
    @GetMapping("/users/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        return userService.getMe(authentication);
    }
}