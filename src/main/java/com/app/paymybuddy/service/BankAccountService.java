package com.app.paymybuddy.service;

import com.app.paymybuddy.model.BankAccount;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BankAccountService {

  private final BankAccountRepository bankAccountRepository;

  @Transactional
  public void createBankAccount(User user) {
    BankAccount bankAccount = new BankAccount();
    bankAccount.setUser(user);
    bankAccount.setBalance(0.0);
    bankAccountRepository.save(bankAccount);
  }
}
