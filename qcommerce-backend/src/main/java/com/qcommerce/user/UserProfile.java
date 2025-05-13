package com.qcommerce.user;

import lombok.Data;
import java.util.List;
import java.util.Set;
import com.qcommerce.security.Role; // Import the Role class

@Data
class UserProfile {
    private Long id;
    private String email;
    private String phone;
    private String locale;
    private List<Address> addresses;
    private Set<Role> roles;

    public UserProfile(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.locale = user.getLocale();
        this.addresses = user.getAddresses();
        this.roles = user.getRoles();
    }
}