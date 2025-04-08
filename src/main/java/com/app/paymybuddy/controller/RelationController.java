package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.RelationDto;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.service.RelationService;
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
public class RelationController {

  private final RelationService relationService;
  private final HandleException handleException;

  @GetMapping("/relation")
  public String showRelation(
    RedirectAttributes redirectAttributes,
    Model model
  ) {
    model.addAttribute("relationDto", new RelationDto());
    return "relation";
  }

  @PostMapping("/relation")
  public String addRelation(
    @Valid @ModelAttribute("relationDto") RelationDto relationDto,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    Authentication authentication
  ) {
    if (bindingResult.hasErrors()) return "relation";

    try {
      relationService.saveRelation(
        relationDto,
        authentication,
        redirectAttributes
      );
    } catch (Exception e) {
      return handleException.exceptionRelation(e, bindingResult);
    }

    return "redirect:/transfer";
  }
}
