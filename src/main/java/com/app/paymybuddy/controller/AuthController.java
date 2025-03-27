package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.service.RegisterService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class AuthController {

  private final RegisterService registerService;

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

    try {
      registerService.saveUser(userRegisterDto);
    } catch (EmailAlreadyUsedException e) {
      result.rejectValue("email", "error.userRegisterDto", e.getMessage());
      return "register";
    }

    return "redirect:/login";
  }
}
