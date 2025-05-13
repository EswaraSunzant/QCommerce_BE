package com.qcommerce.user;

import lombok.Data;

@Data
class PasswordResetRequest {
    private String email;
}