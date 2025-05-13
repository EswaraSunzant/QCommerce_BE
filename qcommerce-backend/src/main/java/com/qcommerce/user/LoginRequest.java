package com.qcommerce.user;

import lombok.Data;

@Data
class LoginRequest {
    private String email;
    private String password;
}