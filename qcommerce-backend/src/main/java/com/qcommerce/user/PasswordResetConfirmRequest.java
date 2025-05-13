package com.qcommerce.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class PasswordResetConfirmRequest {
    private String token;
    private String newPassword;
}