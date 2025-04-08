package com.app.paymybuddy.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class HandleExceptionTest {

  private HandleException handleException;

  @Mock
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    handleException = new HandleException();
  }

  @Test
  void exceptionAuth_WithEmailAlreadyUsedException_ShouldRejectValueAndReturnRegister() {
    String errorMessage = "Email already in use";
    EmailAlreadyUsedException ex = new EmailAlreadyUsedException(errorMessage);

    String result = handleException.exceptionAuth(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.userRegisterDto",
      errorMessage
    );
    assertEquals("register", result);
  }

  @Test
  void exceptionAuth_WithGenericException_ShouldRejectValueAndReturnRegister() {
    Exception ex = new Exception();

    String result = handleException.exceptionAuth(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.userRegisterDto",
      "Une erreur c'est produite"
    );
    assertEquals("register", result);
  }

  @Test
  void exceptionUser_WithUserNotFoundException_ShouldReturnRedirectLogout() {
    UserNotFoundException ex = new UserNotFoundException("User not found");

    String result = handleException.exceptionUser(ex, bindingResult);

    assertEquals("redirect:/logout", result);
  }

  @Test
  void exceptionUser_WithEmailAlreadyUsedException_ShouldRejectValueAndReturnProfil() {
    String errorMessage = "Email already in use";
    EmailAlreadyUsedException ex = new EmailAlreadyUsedException(errorMessage);

    String result = handleException.exceptionUser(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.userUpdateDto",
      errorMessage
    );
    assertEquals("profil", result);
  }

  @Test
  void exceptionUser_WithIllegalArgumentException_ShouldRejectValueAndReturnProfil() {
    String errorMessage = "Invalid password";
    IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

    String result = handleException.exceptionUser(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "password",
      "error.userUpdateDto",
      errorMessage
    );
    assertEquals("profil", result);
  }

  @Test
  void exceptionUser_WithGenericException_ShouldRejectValueAndReturnProfil() {
    Exception ex = new Exception();

    String result = handleException.exceptionUser(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "username",
      "error.userUpdateDto",
      "Une erreur c'est produite"
    );
    assertEquals("profil", result);
  }

  @Test
  void exceptionRelation_WithRelationAlreadyExistsException_ShouldRejectValueAndReturnRelation() {
    String errorMessage = "Relation already exists";
    RelationAlreadyExistsException ex = new RelationAlreadyExistsException(
      errorMessage
    );

    String result = handleException.exceptionRelation(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.relationDto",
      errorMessage
    );
    assertEquals("relation", result);
  }

  @Test
  void exceptionRelation_WithUserNotFoundException_ShouldRejectValueAndReturnRelation() {
    String errorMessage = "User not found";
    UserNotFoundException ex = new UserNotFoundException(errorMessage);

    String result = handleException.exceptionRelation(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.relationDto",
      errorMessage
    );
    assertEquals("relation", result);
  }

  @Test
  void exceptionRelation_WithGenericException_ShouldRejectValueAndReturnRelation() {
    Exception ex = new Exception();

    String result = handleException.exceptionRelation(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.relationDto",
      "Une erreur c'est produite"
    );
    assertEquals("relation", result);
  }

  @Test
  void exceptionTransfer_WithUserNotFoundException_ShouldRejectValueAndReturnTransfer() {
    String errorMessage = "User not found";
    UserNotFoundException ex = new UserNotFoundException(errorMessage);

    String result = handleException.exceptionTransfer(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "email",
      "error.transferDto",
      errorMessage
    );
    assertEquals("transfer", result);
  }

  @Test
  void exceptionTransfer_WithInsufficientBalanceException_ShouldRejectValueAndReturnTransfer() {
    String errorMessage = "Insufficient balance";
    InsufficientBalanceException ex = new InsufficientBalanceException(
      errorMessage
    );

    String result = handleException.exceptionTransfer(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "amount",
      "error.transferDto",
      errorMessage
    );
    assertEquals("transfer", result);
  }

  @Test
  void exceptionTransfer_WithGenericException_ShouldRejectValueAndReturnTransfer() {
    Exception ex = new Exception();

    String result = handleException.exceptionTransfer(ex, bindingResult);

    verify(bindingResult).rejectValue(
      "amount",
      "error.transferDto",
      "Une erreur c'est produite"
    );
    assertEquals("transfer", result);
  }
}
