package com.qcommerce.service;

import com.qcommerce.generated.model.AuthResponse;
import com.qcommerce.generated.model.UserRegistration;
import com.qcommerce.generated.model.RoleReference;
import com.qcommerce.generated.model.LoginRequest; 
import com.qcommerce.model.UserEntity;
import com.qcommerce.model.RoleEntity;
import com.qcommerce.repository.UserRepository;
import com.qcommerce.repository.RoleRepository;
import com.qcommerce.security.JwtTokenProvider; 

import com.qcommerce.exception.InvalidInputException;
import com.qcommerce.exception.UserAlreadyExistsException;

import org.openapitools.jackson.nullable.JsonNullable;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; 
import org.springframework.http.HttpStatus; 

import java.util.HashSet;
import java.util.List; 
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository; 
    private final RoleRepository roleRepository; 
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; 

    @Autowired
    public AuthService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) { 
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider; 
    }

    @Transactional
    public AuthResponse registerUser(UserRegistration registration) {
        if (userRepository.findByEmail(registration.getEmail().toLowerCase()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + registration.getEmail() + " already exists.");
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(registration.getEmail().toLowerCase());
        if (registration.getPhone().isPresent()) {
             newUserEntity.setPhone(registration.getPhone().get());
        }
        newUserEntity.setPasswordHash(passwordEncoder.encode(registration.getPassword()));
        newUserEntity.setActive(true); 

        if (registration.getLocale().isPresent() && registration.getLocale().get() != null) {
            newUserEntity.setLocale(registration.getLocale().get().getValue());
        }
        
        Set<RoleEntity> assignedRoles = new HashSet<>();
        if (registration.getRoles().isPresent() && registration.getRoles().get() != null && !registration.getRoles().get().isEmpty()) {
            List<RoleReference> roleReferences = registration.getRoles().get(); 
            for (int i = 0; i < roleReferences.size(); i++) {
                RoleReference roleRef = roleReferences.get(i);
                final int currentIndex = i; 
                if (roleRef.getId() == null) {
                    throw new InvalidInputException("Role ID cannot be null.", "roles[" + currentIndex + "].id");
                }
                RoleEntity role = roleRepository.findById(roleRef.getId()) 
                    .orElseThrow(() -> new InvalidInputException("Invalid role ID: " + roleRef.getId(),
                                                                "roles[" + currentIndex + "].id"));
                assignedRoles.add(role);
            }
        }
        newUserEntity.setRoles(assignedRoles);

        UserEntity savedUserEntity = userRepository.save(newUserEntity);
        return createAuthResponse(savedUserEntity); 
    }

    @Transactional(readOnly = true) 
    public AuthResponse loginUser(LoginRequest loginRequest) {
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail().toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        if (!userEntity.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User account is inactive.");
        }
        return createAuthResponse(userEntity);
    }

    /**
     * Handles user logout.
     * For stateless JWTs, the primary action is for the client to discard the token.
     * This method can be extended to add the token to a denylist if such a mechanism is implemented.
     * It also clears the Spring Security context.
     */
    public void logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            logger.info("User {} logging out.", authentication.getName());
            // If you implement a token denylist (e.g., using Redis):
            // String token = extractTokenFromRequest(request); // You'd need HttpServletRequest here or from SecurityContext
            // if (token != null) {
            //     tokenDenylistService.addToDenylist(token, jwtTokenProvider.getExpirationDateFromToken(token));
            // }
        } else {
            logger.info("Logout called by an unauthenticated or anonymous user.");
        }
        SecurityContextHolder.clearContext(); // Clear the security context for the current thread
    }


    private AuthResponse createAuthResponse(UserEntity userEntity) {
        com.qcommerce.generated.model.User responseUser = new com.qcommerce.generated.model.User();
        responseUser.setId(userEntity.getId()); 
        responseUser.setEmail(userEntity.getEmail());
        if (userEntity.getPhone() != null) {
            responseUser.setPhone(JsonNullable.of(userEntity.getPhone()));
        }
        if (userEntity.getLocale() != null) {
            responseUser.setLocale(JsonNullable.of(userEntity.getLocale()));
        }
        
        try {
            responseUser.getClass().getMethod("setIsActive", Boolean.class);
            responseUser.setIsActive(userEntity.isActive());
        } catch (NoSuchMethodException e) {
            System.err.println("Warning: Generated User model does not have an setIsActive(Boolean) method. " +
                               "Ensure 'isActive' field is in your OpenAPI User schema and models are regenerated.");
        }

        if (userEntity.getRoles() != null) {
            responseUser.setRoles(JsonNullable.of(
                userEntity.getRoles().stream().map(roleEntity -> {
                    com.qcommerce.generated.model.Role role = new com.qcommerce.generated.model.Role();
                    role.setId(roleEntity.getId()); 
                    role.setName(roleEntity.getName());
                    return role;
                }).collect(Collectors.toList())
            ));
        }

        String accessToken = jwtTokenProvider.generateAccessToken(userEntity);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(JsonNullable.of(refreshToken));
        authResponse.setUser(responseUser);

        return authResponse;
    }

    public Optional<RoleEntity> findRoleById(Long id) { 
        return roleRepository.findById(id);
    }
}
