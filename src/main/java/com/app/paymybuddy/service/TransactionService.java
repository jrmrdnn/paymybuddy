package com.app.paymybuddy.service;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.TransactionRepository;
import com.app.paymybuddy.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
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

    // 2. Find the receiver user by email
    User receiver = userRepository
      .findByEmailAndDeletedAtIsNull(transfer.getEmail())
      .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

    // 3. Create a new transaction
    transactionRepository.save(
      parseAmount(transfer.getAmount()),
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
    return (
      (com.app.paymybuddy.security.CustomUserDetails) authentication.getPrincipal()
    ).getUserId();
  }

  private double parseAmount(String amountStr) throws NumberFormatException {
    return Double.parseDouble(amountStr.replace(',', '.'));
  }

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
