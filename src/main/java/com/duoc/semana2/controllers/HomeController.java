package com.duoc.semana2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String index() {
    return "index"; // templates/index.html
  }

  @GetMapping("/admin")
  public String admin() {
    return "admin"; // templates/admin.html
  }

  @GetMapping("/user")
  public String user() {
    return "user"; // templates/user.html
  }
}
