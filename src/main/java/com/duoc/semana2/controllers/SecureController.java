package com.duoc.semana2.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {
  @GetMapping("/hello")
  public String hello() { return "JWT OK 👋"; }
}
