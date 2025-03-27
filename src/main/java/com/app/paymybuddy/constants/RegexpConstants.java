package com.app.paymybuddy.constants;

public class RegexpConstants {

  /**
   * Regular expression pattern that validates a description.
   *
   * <p>
   * The pattern ensures that the string contains at least one letter or number
   * and may include letters, numbers, spaces, hyphens, periods and apostrophes.
   * </p>
   *
   * <p>
   * The pattern breakdown is as follows:
   * <ul>
   *  <li><code>^</code> - Start of the string</li>
   *  <li><code>(?=.*[\\p{L}\\p{N}])</code> - Positive lookahead to ensure the string contains at least one letter or number</li>
   *  <li><code>[\\p{L}\\p{N}\\s\\-.'']+</code> - Match one or more letters, numbers, spaces, hyphens, periods or apostrophes</li>
   *  <li><code>$</code> - End of the string</li>
   * </ul>
   * </p>
   */
  public static final String DESCRIPTION_REGEXP =
    "^(?=.*[\\p{L}\\p{N}])[\\p{L}\\p{N}\\s\\-.'']+$";

  /**
   * Regular expression pattern that validates an amount.
   *
   * <p>
   * The pattern ensures that the string contains a positive number with an optional decimal part.
   * </p>
   *
   * <p>
   * The pattern breakdown is as follows:
   * <ul>
   *  <li><code>^</code> - Start of the string</li>
   *  <li><code>\\d+</code> - Match one or more digits</li>
   *  <li><code>([,]\\d{1,2})?</code> - Match a comma followed by one or two digits zero or one time</li>
   *  <li><code>$</code> - End of the string</li>
   * </ul>
   * </p>
   */
  public static final String AMOUNT_REGEXP = "^\\d+([,]\\d{1,2})?$";

  /**
   * Regular expression pattern that validates a password.
   *
   * <p>
   * The pattern ensures that the string contains at least one lowercase letter, one uppercase letter,
   * one number and one special character and may include letters, numbers and special characters.
   * </p>
   *
   * <p>
   * The pattern breakdown is as follows:
   * <ul>
   *  <li><code>^</code> - Start of the string</li>
   *  <li><code>(?=.*[\\p{L}\\p{N}])</code> - Positive lookahead to ensure the string contains at least one letter or number</li>
   *  <li><code>(?=.*[\\p{Lu}])</code> - Positive lookahead to ensure the string contains at least one uppercase letter</li>
   *  <li><code>(?=.*[\\p{Ll}])</code> - Positive lookahead to ensure the string contains at least one lowercase letter</li>
   *  <li><code>(?=.*[\\p{Punct}])</code> - Positive lookahead to ensure the string contains at least one special character</li>
   *  <li><code>[\\p{L}\\p{N}\\p{Punct}]+$</code> - Match one or more letters, numbers or special characters</li>
   *  <li><code>$</code> - End of the string</li>
   * </ul>
   * </p>
   */
  public static final String PASSWORD_REGEXP =
    "^(?=.*[\\p{L}\\p{N}])(?=.*[\\p{Lu}])(?=.*[\\p{Ll}])(?=.*[\\p{Punct}])[\\p{L}\\p{N}\\p{Punct}]+$";
}
