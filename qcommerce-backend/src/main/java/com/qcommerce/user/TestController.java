package com.qcommerce.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String hello() {
        System.out.println(">>> /test endpoint hit");
        return "Server is working!";
    }
}
