package com.app.paymybuddy.exception;

public class RelationAlreadyExistsException extends RuntimeException {

  /**
   * Exception thrown when the relation already exists
   *
   * @param message the message to display
   */
  public RelationAlreadyExistsException(String message) {
    super(message);
  }
}
