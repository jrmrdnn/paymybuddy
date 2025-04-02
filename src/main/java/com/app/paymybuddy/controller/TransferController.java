package com.app.paymybuddy.controller;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.dto.response.UserRelationDto;
import com.app.paymybuddy.exception.InsufficientBalanceException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.service.RelationService;
import com.app.paymybuddy.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class TransferController {

  private final RelationService relationService;
  private final TransactionService transactionService;

  @GetMapping("/transfer")
  public String showTransferPage(
    RedirectAttributes redirectAttributes,
    Authentication authentication,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "5") int size,
    Model model
  ) {
    Pageable pageable = PageRequest.of(page, size);
    model.addAttribute("transferDto", new TransferDto());
    addModelAttributes(authentication, model, pageable);
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
      addModelAttributes(authentication, model);
      return "transfer";
    }

    try {
      transactionService.saveTransaction(authentication, transfer);
      redirectAttributes.addFlashAttribute(
        "successMessage",
        "Transfert effectué avec succès !!"
      );
    } catch (UserNotFoundException e) {
      bindingResult.rejectValue("email", "error.transferDto", e.getMessage());
      addModelAttributes(authentication, model);
      return "transfer";
    } catch (InsufficientBalanceException e) {
      bindingResult.rejectValue("amount", "error.transferDto", e.getMessage());
      addModelAttributes(authentication, model);
      return "transfer";
    } catch (Exception e) {
      bindingResult.rejectValue(
        "amount",
        "error.transferDto",
        "Une erreur c'est produite"
      );
      addModelAttributes(authentication, model);
      return "transfer";
    }

    return "redirect:/transfer";
  }

  private void addModelAttributes(Authentication authentication, Model model) {
    Set<UserRelationDto> relations = relationService.findUserRelations(
      authentication
    );
    List<Transaction> transactions = transactionService.getTransactionHistory(
      authentication,
      PageRequest.of(0, 5)
    );
    model.addAttribute("relations", relations);
    model.addAttribute("transactions", transactions);
  }

  private void addModelAttributes(
    Authentication authentication,
    Model model,
    Pageable pageable
  ) {
    Set<UserRelationDto> relations = relationService.findUserRelations(
      authentication
    );
    List<Transaction> transactions = transactionService.getTransactionHistory(
      authentication,
      pageable
    );
    model.addAttribute("relations", relations);
    model.addAttribute("transactions", transactions);
  }
}
