package com.app.paymybuddy.exception;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class HandleException {

  /**
   * Handle exception for user registration
   * @param e
   * @param bindingResult
   * @return
   */
  public String exceptionAuth(Exception e, BindingResult bindingResult) {
    if (e instanceof EmailAlreadyUsedException) {
      bindingResult.rejectValue(
        "email",
        "error.userRegisterDto",
        e.getMessage()
      );
    } else {
      bindingResult.rejectValue(
        "email",
        "error.userRegisterDto",
        "Une erreur c'est produite"
      );
    }

    return "register";
  }

  /**
   * Handle exception for user update
   * @param e
   * @param bindingResult
   * @return
   */
  public String exceptionUser(Exception e, BindingResult bindingResult) {
    if (e instanceof UserNotFoundException) return "redirect:/logout";
    else if (e instanceof EmailAlreadyUsedException) {
      bindingResult.rejectValue("email", "error.userUpdateDto", e.getMessage());
    } else if (e instanceof IllegalArgumentException) {
      bindingResult.rejectValue(
        "password",
        "error.userUpdateDto",
        e.getMessage()
      );
    } else {
      bindingResult.rejectValue(
        "username",
        "error.userUpdateDto",
        "Une erreur c'est produite"
      );
    }

    return "profil";
  }

  /**
   * Handle exception for relation
   * @param e
   * @param bindingResult
   * @return
   */
  public String exceptionRelation(Exception e, BindingResult bindingResult) {
    if (e instanceof RelationAlreadyExistsException) {
      bindingResult.rejectValue("email", "error.relationDto", e.getMessage());
    } else if (e instanceof UserNotFoundException) {
      bindingResult.rejectValue("email", "error.relationDto", e.getMessage());
    } else {
      bindingResult.rejectValue(
        "email",
        "error.relationDto",
        "Une erreur c'est produite"
      );
    }

    return "relation";
  }

  /**
   * Handle exception for transfer
   * @param e
   * @param bindingResult
   * @return
   */
  public String exceptionTransfer(Exception e, BindingResult bindingResult) {
    if (e instanceof UserNotFoundException) {
      bindingResult.rejectValue("email", "error.transferDto", e.getMessage());
    } else if (e instanceof InsufficientBalanceException) {
      bindingResult.rejectValue("amount", "error.transferDto", e.getMessage());
    } else {
      bindingResult.rejectValue(
        "amount",
        "error.transferDto",
        "Une erreur c'est produite"
      );
    }

    return "transfer";
  }
}
