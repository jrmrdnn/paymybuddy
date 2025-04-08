package com.app.paymybuddy.exception;

public class EmailAlreadyUsedException extends RuntimeException {

  /**
   * Exception thrown when the email is already used
   *
   * @param message the message to display
   */
  public EmailAlreadyUsedException(String message) {
    super(message);
  }
}
