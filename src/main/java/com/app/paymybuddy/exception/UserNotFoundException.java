package com.app.paymybuddy.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException() {
    super("Utilisateur non trouvé");
  }
}
