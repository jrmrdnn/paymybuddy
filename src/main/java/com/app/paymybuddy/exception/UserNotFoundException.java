package com.app.paymybuddy.exception;

public class UserNotFoundException extends RuntimeException {

  /**
   * Exception thrown when the user is not found
   *
   * @param message the message to display
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
