package com.duoc.semana2.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    @GetMapping("/admin")
    public String admin() { return "admin"; }

    @GetMapping("/user")
    public String user() { return "user"; }

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/api/secure/test")
    public String secureApi() { return "secure"; }
}
