package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.service.TransactionService;
import com.app.paymybuddy.util.ModelAttributes;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class TransferController {

  private final TransactionService transactionService;
  private final HandleException handleException;
  private final ModelAttributes modelAttributes;

  @GetMapping("/transfer")
  public String showTransferPage(
    RedirectAttributes redirectAttributes,
    Authentication authentication,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "5") int size,
    Model model
  ) {
    model.addAttribute("transferDto", new TransferDto());
    modelAttributes.addAttributeTransfer(
      authentication,
      model,
      PageRequest.of(page, size)
    );
    return "transfer";
  }

  @PostMapping("/transfer")
  public String addTransfer(
    @Valid @ModelAttribute("transferDto") TransferDto transfer,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    Authentication authentication,
    Model model
  ) {
    if (bindingResult.hasErrors()) {
      modelAttributes.addAttributeTransfer(authentication, model);
      return "transfer";
    }

    try {
      transactionService.saveTransaction(authentication, transfer);
      redirectAttributes.addFlashAttribute(
        "successMessage",
        "Transfert effectué avec succès !!"
      );
    } catch (Exception exception) {
      modelAttributes.addAttributeTransfer(authentication, model);
      return handleException.exceptionTransfer(exception, bindingResult);
    }

    return "redirect:/transfer";
  }

  /**
   * Handle MethodArgumentTypeMismatchException
   * @param ex
   * @param redirectAttributes
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public String handleTypeMismatch(
    MethodArgumentTypeMismatchException ex,
    RedirectAttributes redirectAttributes
  ) {
    redirectAttributes.addFlashAttribute(
      "errorMessage",
      "Paramètre de pagination invalide."
    );

    return "redirect:/transfer";
  }
}
