package com.app.paymybuddy.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankAccountTest {

  private BankAccount bankAccount;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1);
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPassword("password");

    bankAccount = new BankAccount();
    bankAccount.setUserId(1);
    bankAccount.setUser(user);
    bankAccount.setBalance(1000.0);
    bankAccount.setAccountNumber("123456789");
    bankAccount.setIban("FR76123456789");
    bankAccount.setBic("ABCDEFGH");
  }

  @Test
  void testBankAccountCreation() {
    assertNotNull(bankAccount);
    assertEquals(1, bankAccount.getUserId());
    assertEquals(user, bankAccount.getUser());
    assertEquals(1000.0, bankAccount.getBalance());
    assertEquals("123456789", bankAccount.getAccountNumber());
    assertEquals("FR76123456789", bankAccount.getIban());
    assertEquals("ABCDEFGH", bankAccount.getBic());
  }

  @Test
  void testOnCreateSetsCreatedAt() {
    bankAccount.onCreate();
    assertNotNull(bankAccount.getCreatedAt());
    assertTrue(
      ChronoUnit.SECONDS.between(
        LocalDateTime.now(),
        bankAccount.getCreatedAt()
      ) <
      1
    );
  }

  @Test
  void testOnUpdateSetsUpdatedAt() {
    bankAccount.onUpdate();
    assertNotNull(bankAccount.getUpdatedAt());
    assertTrue(
      ChronoUnit.SECONDS.between(
        LocalDateTime.now(),
        bankAccount.getUpdatedAt()
      ) <
      1
    );
  }

  @Test
  void testEqualsAndHashCode() {
    BankAccount sameBankAccount = new BankAccount();
    sameBankAccount.setUserId(1);
    sameBankAccount.setUser(user);
    sameBankAccount.setBalance(1000.0);
    sameBankAccount.setAccountNumber("123456789");
    sameBankAccount.setIban("FR76123456789");
    sameBankAccount.setBic("ABCDEFGH");

    assertEquals(bankAccount, sameBankAccount);
    assertEquals(bankAccount.hashCode(), sameBankAccount.hashCode());

    BankAccount differentBankAccount = new BankAccount();
    differentBankAccount.setUserId(2);
    differentBankAccount.setBalance(2000.0);
    differentBankAccount.setAccountNumber("987654321");

    assertNotEquals(bankAccount, differentBankAccount);
    assertNotEquals(bankAccount.hashCode(), differentBankAccount.hashCode());
  }

  @Test
  void testToString() {
    String toString = bankAccount.toString();
    assertNotNull(toString);
    assertTrue(toString.contains("userId=1"));
    assertTrue(toString.contains("balance=1000.0"));
    assertTrue(toString.contains("accountNumber=123456789"));
    assertTrue(toString.contains("iban=FR76123456789"));
    assertTrue(toString.contains("bic=ABCDEFGH"));
  }

  @Test
  void testSetAndGetBalance() {
    bankAccount.setBalance(2000.0);
    assertEquals(2000.0, bankAccount.getBalance());
  }

  @Test
  void testSetAndGetAccountNumber() {
    bankAccount.setAccountNumber("987654321");
    assertEquals("987654321", bankAccount.getAccountNumber());
  }

  @Test
  void testSetAndGetIban() {
    bankAccount.setIban("GB29NWBK60161331926819");
    assertEquals("GB29NWBK60161331926819", bankAccount.getIban());
  }

  @Test
  void testSetAndGetBic() {
    bankAccount.setBic("DEUTDEFF");
    assertEquals("DEUTDEFF", bankAccount.getBic());
  }
}
