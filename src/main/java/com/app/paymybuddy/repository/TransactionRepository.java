package com.app.paymybuddy.repository;

import com.app.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
  extends JpaRepository<Transaction, Integer> {
  /**
   * Find all transactions of a user
   * @param userId
   * @return List<Transaction>
   */
  @Query(
    "SELECT t FROM Transaction t " +
    "WHERE t.receiver.id = :userId OR t.sender.id = :userId " +
    "ORDER BY t.id DESC"
  )
  Page<Transaction> findTransactionsByUserId(
    @Param("userId") int userId,
    Pageable pageable
  );

  /**
   * Save a transaction
   * @param currentUserId
   * @param receiverUserId
   * @param transaction
   */
  @Modifying
  @Query(
    "INSERT INTO Transaction (amount, description, sender, receiver) " +
    "SELECT :amount, :description, u1, u2 FROM User u1, User u2 " +
    "WHERE u1.id = :currentUserId AND u2.id = :receiverUserId"
  )
  void save(
    @Param("amount") double amount,
    @Param("description") String description,
    @Param("currentUserId") int currentUserId,
    @Param("receiverUserId") int receiverUserId
  );
}
