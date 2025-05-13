package com.qcommerce.user;

import lombok.Data;
import lombok.AllArgsConstructor; // Add this import

@Data
@AllArgsConstructor // Add this annotation
class LoginResponse {
    private String token;
    private String refreshToken;
}