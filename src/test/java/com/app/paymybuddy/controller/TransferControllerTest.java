package com.app.paymybuddy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.exception.InsufficientBalanceException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.service.RelationService;
import com.app.paymybuddy.service.TransactionService;
import com.app.paymybuddy.util.ModelAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
  private BindingResult bindingResult;

  @Mock
  private RedirectAttributes redirectAttributes;

  @Mock
  private HandleException handleException;

  @Mock
  private ModelAttributes modelAttributes;

  @InjectMocks
  private TransferController transferController;

  @Test
  public void showTransferPage_ShouldAddAttributesAndReturnTransferView() {
    doNothing()
      .when(modelAttributes)
      .addAttributeTransfer(eq(authentication), eq(model), any(Pageable.class));

    String viewName = transferController.showTransferPage(
      redirectAttributes,
      authentication,
      0,
      10,
      model
    );

    verify(modelAttributes).addAttributeTransfer(
      eq(authentication),
      eq(model),
      any(Pageable.class)
    );
    verify(model).addAttribute(eq("transferDto"), any(TransferDto.class));

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithBindingErrors_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();

    when(bindingResult.hasErrors()).thenReturn(true);

    doNothing()
      .when(modelAttributes)
      .addAttributeTransfer(eq(authentication), eq(model));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(bindingResult).hasErrors();
    verify(modelAttributes).addAttributeTransfer(authentication, model);

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
    Exception exception = new UserNotFoundException("User not found");

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(exception)
      .when(transactionService)
      .saveTransaction(any(Authentication.class), any(TransferDto.class));

    when(
      handleException.exceptionTransfer(
        any(UserNotFoundException.class),
        eq(bindingResult)
      )
    ).thenReturn("transfer");

    doNothing()
      .when(modelAttributes)
      .addAttributeTransfer(eq(authentication), eq(model));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(handleException).exceptionTransfer(
      any(UserNotFoundException.class),
      eq(bindingResult)
    );
    verify(modelAttributes).addAttributeTransfer(authentication, model);

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithInsufficientBalanceException_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();
    Exception exception = new InsufficientBalanceException(
      "Insufficient balance"
    );

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(exception)
      .when(transactionService)
      .saveTransaction(any(Authentication.class), any(TransferDto.class));

    when(
      handleException.exceptionTransfer(
        any(InsufficientBalanceException.class),
        eq(bindingResult)
      )
    ).thenReturn("transfer");

    doNothing()
      .when(modelAttributes)
      .addAttributeTransfer(eq(authentication), eq(model));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(handleException).exceptionTransfer(
      any(InsufficientBalanceException.class),
      eq(bindingResult)
    );
    verify(modelAttributes).addAttributeTransfer(authentication, model);

    assert "transfer".equals(viewName);
  }

  @Test
  public void addTransfer_WithGenericException_ShouldReturnTransferView() {
    TransferDto transferDto = new TransferDto();
    Exception exception = new RuntimeException("Error");

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(exception)
      .when(transactionService)
      .saveTransaction(any(Authentication.class), any(TransferDto.class));

    when(
      handleException.exceptionTransfer(
        any(RuntimeException.class),
        eq(bindingResult)
      )
    ).thenReturn("transfer");

    doNothing()
      .when(modelAttributes)
      .addAttributeTransfer(eq(authentication), eq(model));

    String viewName = transferController.addTransfer(
      transferDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    verify(handleException).exceptionTransfer(
      any(RuntimeException.class),
      eq(bindingResult)
    );
    verify(modelAttributes).addAttributeTransfer(authentication, model);

    assert "transfer".equals(viewName);
  }

  @Test
  public void handleTypeMismatch_ShouldAddErrorMessageAndRedirectToTransfer() {
    MethodArgumentTypeMismatchException ex = mock(
      MethodArgumentTypeMismatchException.class
    );

    String viewName = transferController.handleTypeMismatch(
      ex,
      redirectAttributes
    );

    verify(redirectAttributes).addFlashAttribute(
      "errorMessage",
      "Paramètre de pagination invalide."
    );

    assert "redirect:/transfer".equals(viewName);
  }
}
