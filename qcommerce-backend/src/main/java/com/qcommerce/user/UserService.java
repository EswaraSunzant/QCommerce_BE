package com.qcommerce.user;

import com.qcommerce.security.JwtService;
import com.qcommerce.security.Role;
import com.qcommerce.security.RoleRepository; // Import RoleRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional; // Import for transactions

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;


    @Transactional // Add transactional annotation for managing database transactions
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            return ResponseEntity.badRequest().body("Phone number is already taken");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles()); // No changes needed here
        user.setLocale(request.getLocale());

        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(new UserProfile(savedUser));
    }

    public ResponseEntity<?> loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication); // Generate refresh token

        return ResponseEntity.ok(new LoginResponse(jwt, refreshToken));
    }

    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok().body("Logged out successfully");
    }

    public ResponseEntity<?> resetPasswordRequest(PasswordResetRequest request) {
        // Implement logic to generate a reset token and send email
        //  Omitted for brevity
        return ResponseEntity.ok().body("Password reset email sent");
    }

    public ResponseEntity<?> resetPasswordConfirm(PasswordResetConfirmRequest request) {
        // Implement logic to verify the token and update the password
        // Omitted for brevity
        return ResponseEntity.ok().body("Password reset successful");
    }

    public ResponseEntity<?> getMe(Authentication authentication) {
        User user = (User) authentication.getPrincipal(); //Spring security stores the user in principal
        return ResponseEntity.ok(new UserProfile(user));
    }
}