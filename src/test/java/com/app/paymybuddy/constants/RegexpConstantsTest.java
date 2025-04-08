package com.app.paymybuddy.constants;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RegexpConstantsTest {

  @Nested
  class DescriptionRegexpTests {

    @ParameterizedTest
    @ValueSource(
      strings = {
        "Valid description",
        "Description with numbers 123",
        "Description-with-hyphens",
        "Description.with.periods",
        "Description's with apostrophes",
        "Mixed 123 - . ' characters",
        "A",
        "1",
        "  a   ",
      }
    )
    void shouldMatchValidDescriptions(String input) {
      assertTrue(input.matches(RegexpConstants.DESCRIPTION_REGEXP));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "   ", "!@#$%^&*()_+", "\n\t" })
    void shouldNotMatchInvalidDescriptions(String input) {
      assertFalse(input.matches(RegexpConstants.DESCRIPTION_REGEXP));
    }
  }

  @Nested
  class AmountRegexpTests {

    @ParameterizedTest
    @ValueSource(strings = { "0", "1", "123", "1234567890", "123,4", "123,45" })
    void shouldMatchValidAmounts(String input) {
      assertTrue(input.matches(RegexpConstants.AMOUNT_REGEXP));
    }

    @ParameterizedTest
    @ValueSource(
      strings = {
        "",
        "-1",
        "1.45",
        "123,456",
        "123,",
        "123,,45",
        "123a",
        "123€",
        "1 234",
        ",45",
      }
    )
    void shouldNotMatchInvalidAmounts(String input) {
      assertFalse(input.matches(RegexpConstants.AMOUNT_REGEXP));
    }
  }

  @Nested
  class PasswordRegexpTests {

    @ParameterizedTest
    @ValueSource(
      strings = {
        "Password1!",
        "aB1!",
        "Complex@Password123",
        "Very.Complex-Password#1",
        "p@Ssw0rd",
        "MyP@ssw0rd!",
      }
    )
    void shouldMatchValidPasswords(String input) {
      assertTrue(input.matches(RegexpConstants.PASSWORD_REGEXP));
    }

    @Test
    void shouldNotMatchPasswordsMissingUppercase() {
      assertFalse("password1!".matches(RegexpConstants.PASSWORD_REGEXP));
    }

    @Test
    void shouldNotMatchPasswordsMissingLowercase() {
      assertFalse("PASSWORD1!".matches(RegexpConstants.PASSWORD_REGEXP));
    }

    @Test
    void shouldNotMatchPasswordsMissingDigit() {
      assertFalse("Password!".matches(RegexpConstants.PASSWORD_REGEXP));
    }

    @Test
    void shouldNotMatchPasswordsMissingSpecial() {
      assertFalse("Password1".matches(RegexpConstants.PASSWORD_REGEXP));
    }

    @ParameterizedTest
    @ValueSource(
      strings = { "", " ", "short", "123456", "!@#$%^", "password", "PASSWORD" }
    )
    void shouldNotMatchOtherInvalidPasswords(String input) {
      assertFalse(input.matches(RegexpConstants.PASSWORD_REGEXP));
    }
  }
}
