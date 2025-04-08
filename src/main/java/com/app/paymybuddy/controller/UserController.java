package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.UserUpdateDto;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class UserController {

  private final UserService userService;
  private final HandleException handleException;

  @GetMapping("/profil")
  public String showProfil(
    RedirectAttributes redirectAttributes,
    Authentication authentication,
    Model model
  ) {
    model.addAttribute("userUpdateDto", userService.findById(authentication));
    return "profil";
  }

  @PostMapping("/profil")
  public String updateProfil(
    @Valid @ModelAttribute("userUpdateDto") UserUpdateDto userUpdateDto,
    BindingResult result,
    RedirectAttributes redirectAttributes,
    Authentication authentication,
    Model model
  ) {
    if (result.hasErrors()) return "profil";

    try {
      userService.updateUser(authentication, userUpdateDto);
      redirectAttributes.addFlashAttribute(
        "successMessage",
        "Profil mis à jour avec succès !!"
      );
    } catch (Exception e) {
      return handleException.exceptionUser(e, result);
    }
    return "redirect:/transfer";
  }
}
