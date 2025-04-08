package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.service.RegisterService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class AuthController {

  private final RegisterService registerService;
  private final HandleException handleException;

  @GetMapping("/login")
  public String showLogin(RedirectAttributes redirectAttributes) {
    return "login";
  }

  @GetMapping("/register")
  public String showRegister(Model model) {
    model.addAttribute("userRegisterDto", new UserRegisterDto());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(
    @Valid @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto,
    BindingResult result,
    RedirectAttributes redirectAttributes
  ) {
    if (result.hasErrors()) return "register";

    try {
      registerService.saveUser(userRegisterDto);
      redirectAttributes.addFlashAttribute(
        "successMessage",
        "Inscription réussie ! Vous pouvez vous connecter."
      );
    } catch (Exception e) {
      return handleException.exceptionAuth(e, result);
    }

    return "redirect:/login";
  }
}
