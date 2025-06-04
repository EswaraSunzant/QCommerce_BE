package com.qcommerce; // Updated package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the QCommerce service.
 */
@SpringBootApplication // Scans com.qcommerce and its subpackages by default
public class QcommerceApplication { // Renamed class

    public static void main(String[] args) {
        SpringApplication.run(QcommerceApplication.class, args);
    }

}
