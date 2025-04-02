package com.app.paymybuddy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.dto.response.UserRelationDto;
import com.app.paymybuddy.exception.InsufficientBalanceException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.service.RelationService;
import com.app.paymybuddy.service.TransactionService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {

  @Mock
  private TransactionService transactionService;

  @Mock
  private RelationService relationService;

  @Mock
  private Authentication authentication;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectAttributes;

  @Mock
  private BindingResult bindingResult;

  @InjectMocks
  private TransferController transferController;

  @Test
  public void showTransferPage_ShouldAddAttributesAndReturnTransferView() {
    Set<UserRelationDto> relations = new HashSet<>();
    List<Transaction> transactions = new ArrayList<>();

    when(
      relationService.findUserRelations(any(Authentication.class))
    ).thenReturn(relations);
    when(
      transactionService.getTransactionHistory(
        any(Authentication.class),
        any(Pageable.class)
      )
    ).thenReturn(transactions);

    String viewName = transferController.showTransferPage(
      redirectAttributes,
      authentication,
      0,
      10,
      model
    );

    verify(model).addAttribute(eq("transferDto"), any(TransferDto.class));
    verify(model).addAttribute("relations", relations);
    verify(model).addAttribute("transactions", transactions);
    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(
      eq(authentication),
      any(Pageable.class)
    );

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithBindingErrors_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();

    when(bindingResult.hasErrors()).thenReturn(true);

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(bindingResult).hasErrors();
    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(
      eq(authentication),
      any(Pageable.class)
    );

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_SuccessfulTransfer_ShouldRedirect() {
    TransferDto transferDto = new TransferDto();
    when(bindingResult.hasErrors()).thenReturn(false);

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(transactionService).saveTransaction(authentication, transferDto);
    verify(redirectAttributes).addFlashAttribute(
      "successMessage",
      "Transfert effectué avec succès !!"
    );

    assert "redirect:/transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithUserNotFoundException_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new UserNotFoundException())
      .when(transactionService)
      .saveTransaction(any(Authentication.class), any(TransferDto.class));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(bindingResult).rejectValue(
      "email",
      "error.transferDto",
      "Utilisateur non trouvé"
    );
    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(
      eq(authentication),
      any(Pageable.class)
    );

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithInsufficientBalanceException_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new InsufficientBalanceException("Insufficient balance"))
      .when(transactionService)
      .saveTransaction(any(Authentication.class), any(TransferDto.class));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(bindingResult).rejectValue(
      "amount",
      "error.transferDto",
      "Insufficient balance"
    );
    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(
      eq(authentication),
      any(Pageable.class)
    );

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithGenericException_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new RuntimeException("Generic error"))
      .when(transactionService)
      .saveTransaction(any(Authentication.class), any(TransferDto.class));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(bindingResult).rejectValue(
      "amount",
      "error.transferDto",
      "Une erreur c'est produite"
    );
    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(
      eq(authentication),
      any(Pageable.class)
    );

    assert "transfer".equals(viewName);
  }
}
