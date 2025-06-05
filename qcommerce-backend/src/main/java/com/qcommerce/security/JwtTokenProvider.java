package com.qcommerce.security; // Or your preferred package for security utilities

import com.qcommerce.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.access-token.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private SecretKey jwtSecretKey;

    @PostConstruct
    protected void init() {
        // Ensure the secret key is strong enough for the chosen algorithm (HS256 requires at least 256 bits)
        // For production, use a securely generated and stored key.
        if (jwtSecretString == null || jwtSecretString.length() < 32) { // 32 bytes = 256 bits
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes long for HS256.");
        }
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
    }

    public String generateAccessToken(UserEntity userEntity) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userEntity.getId());
        claims.put("email", userEntity.getEmail());
        claims.put("roles", userEntity.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()));
        // Add any other claims you need

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userEntity.getEmail()) // or userEntity.getId().toString()
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity userEntity) {
        // Refresh tokens typically have fewer claims, mainly identifying the user for re-authentication.
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userEntity.getId());
        // You might add a unique token ID (jti) if you plan to store and manage refresh tokens (e.g., for revocation)

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userEntity.getEmail()) // or userEntity.getId().toString()
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // You would also add methods here to validate tokens and get claims from tokens
    // if this provider is also used for token validation in your security filter.
    // For example:
    /*
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // Log error (e.g., MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException)
            System.err.println("Invalid JWT token: " + ex.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token).getBody();
    }
    */
}
