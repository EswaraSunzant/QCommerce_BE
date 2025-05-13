package com.qcommerce.user;

import com.qcommerce.security.Role;
import lombok.Data;
import java.util.Set;

@Data
class RegisterRequest {
    private String email;
    private String phone;
    private String password;
    private Set<Role> roles;
    private String locale;
}