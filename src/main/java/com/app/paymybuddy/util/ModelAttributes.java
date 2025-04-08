package com.app.paymybuddy.util;

import com.app.paymybuddy.dto.response.UserRelationDto;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.service.RelationService;
import com.app.paymybuddy.service.TransactionService;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
@AllArgsConstructor
public class ModelAttributes {

  private final RelationService relationService;
  private final TransactionService transactionService;

  /**
   * Add model attributes for the transfer page.
   * @param authentication
   * @param model
   */
  public void addAttributeTransfer(Authentication authentication, Model model) {
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

  /**
   * Add model attributes for the transfer page with pagination.
   * @param authentication
   * @param model
   * @param pageable
   */
  public void addAttributeTransfer(
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
