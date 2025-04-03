package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import com.app.paymybuddy.model.BankAccount;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Test
  void createBankAccount_shouldCreateBankAccountWithUserAndZeroBalance() {
    User user = new User();
    user.setId(1);
    user.setEmail("test@example.com");

    BankAccountService bankAccountService = new BankAccountService(
      bankAccountRepository
    );

    bankAccountService.createBankAccount(user);

    ArgumentCaptor<BankAccount> bankAccountCaptor = ArgumentCaptor.forClass(
      BankAccount.class
    );
    verify(bankAccountRepository).save(bankAccountCaptor.capture());

    BankAccount savedBankAccount = bankAccountCaptor.getValue();
    assertNotNull(savedBankAccount);
    assertEquals(user, savedBankAccount.getUser());
    assertEquals(0.0, savedBankAccount.getBalance());
  }
}
