package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

  @GetMapping("/login")
  public String showLogin() {
    return "login";
  }

  @GetMapping("/register")
  public String showRegisterForm(Model model) {
    model.addAttribute("userRegisterDto", new UserRegisterDto());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(
    @Valid @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto,
    BindingResult result
  ) {
    if (result.hasErrors()) return "register";

    return "redirect:/login";
  }
}
