package com.app.paymybuddy.service;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.exception.InsufficientBalanceException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.BankAccountRepository;
import com.app.paymybuddy.repository.TransactionRepository;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final BankAccountRepository bankAccountRepository;
  private final UserRepository userRepository;

  /**
   * Find the transactions of the current user.
   * @param authentication
   * @return List<Transaction>
   */
  @Transactional
  public List<Transaction> getTransactionHistory(
    Authentication authentication,
    Pageable pageable
  ) {
    // 1. Get the current user id
    Integer currentUserId = getCurrentUserId(authentication);

    // 2. Find the transactions of the current user
    Page<Transaction> transactions =
      transactionRepository.findTransactionsByUserId(currentUserId, pageable);

    // 3. Transform the transactions to show the correct sender and receiver
    return transformTransactionList(currentUserId, transactions);
  }

  /**
   * Save a new transaction.
   * @param authentication
   * @param transfer
   */
  @Transactional
  public void saveTransaction(
    Authentication authentication,
    TransferDto transfer
  ) {
    // 1. Get the current user id
    Integer currentUserId = getCurrentUserId(authentication);

    // 2. Check amount is valid
    double transferAmount = parseAmount(transfer.getAmount());
    checkIfAmountIsValid(transferAmount);

    // 3. Find the receiver user by email
    User receiver = userRepository
      .findByEmailAndDeletedAtIsNull(transfer.getEmail())
      .orElseThrow(() -> new UserNotFoundException());

    // 4. Calculate the net transaction amount for the current user
    double transaction =
      transactionRepository.calculateNetTransactionAmountByUserId(
        currentUserId
      );

    // 5. Find the bank account of the current user
    double bankAccount = bankAccountRepository
      .findByUserId(currentUserId)
      .orElseThrow(() -> new UserNotFoundException())
      .getBalance();

    // 6. Check if the user has enough balance
    checkIfSufficientBalance(
      currentUserId,
      bankAccount,
      transferAmount,
      transaction
    );

    // 7. Create a new transaction
    transactionRepository.save(
      transferAmount,
      transfer.getDescription(),
      currentUserId,
      receiver.getId()
    );
  }

  /**
   * Get the current user id.
   * @param authentication
   * @return Integer
   */
  private Integer getCurrentUserId(Authentication authentication) {
    return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
  }

  /**
   * Parse the amount from a string to a double.
   * @param amountStr
   * @return double
   */
  private double parseAmount(String amountStr) throws NumberFormatException {
    return Double.parseDouble(amountStr.replace(',', '.'));
  }

  /**
   * Check if the amount is valid.
   * @param amount
   */
  private void checkIfAmountIsValid(double amount) {
    log.info("Transfer amount: {}", amount);
    if (amount <= 0) {
      throw new InsufficientBalanceException(
        "Le montant doit être supérieur à 0"
      );
    }
  }

  /**
   * Check if the user has enough balance.
   * @param currentUserId
   * @param bankAccount
   * @param transferAmount
   * @param transaction
   */
  private void checkIfSufficientBalance(
    Integer currentUserId,
    double bankAccount,
    double transferAmount,
    double transaction
  ) {
    log.info(
      "🏛️ Bank account balance for user {}: {}",
      currentUserId,
      bankAccount
    );
    log.info(
      "💸 Net transaction amount for user {}: {}",
      currentUserId,
      transaction
    );
    double cash = bankAccount + transaction;
    log.info("💰 Cash available for user {}: {}", currentUserId, cash);
    if (cash < transferAmount) {
      log.error(
        "⚠️ Insufficient balance for user {}: {} < {}",
        currentUserId,
        cash,
        transferAmount
      );
      throw new InsufficientBalanceException(
        "Le solde de votre compte est insuffisant pour effectuer cette opération"
      );
    }
    log.info(
      "💰 New cash available for user {}: {}",
      currentUserId,
      cash - transferAmount
    );
  }

  /**
   * Transform the transactions to show the correct sender and receiver.
   * @param currentUserId
   * @param transactions
   * @return List<Transaction>
   */
  private List<Transaction> transformTransactionList(
    Integer currentUserId,
    Page<Transaction> transactions
  ) {
    List<Transaction> modifiedTransactions = new ArrayList<>();

    for (Transaction transaction : transactions) {
      Transaction modifiedTransaction = new Transaction();
      modifiedTransaction.setId(transaction.getId());
      modifiedTransaction.setDescription(transaction.getDescription());
      modifiedTransaction.setAmount(transaction.getAmount());
      modifiedTransaction.setCreatedAt(transaction.getCreatedAt());

      if (transaction.getSender().getId() == currentUserId) {
        modifiedTransaction.setAmount(transaction.getAmount() * -1);
        modifiedTransaction.setSender(transaction.getReceiver());
        modifiedTransaction.setReceiver(transaction.getSender());
      } else {
        modifiedTransaction.setSender(transaction.getSender());
        modifiedTransaction.setReceiver(transaction.getReceiver());
      }

      modifiedTransactions.add(modifiedTransaction);
    }
    return modifiedTransactions;
  }
}
