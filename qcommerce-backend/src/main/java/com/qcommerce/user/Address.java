package com.qcommerce.user;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String type;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
