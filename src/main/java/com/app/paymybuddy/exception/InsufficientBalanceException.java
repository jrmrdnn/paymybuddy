package com.app.paymybuddy.exception;

public class InsufficientBalanceException extends RuntimeException {

  /**
   * Exception thrown when the user has insufficient balance
   *
   * @param message the message to display
   */
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
