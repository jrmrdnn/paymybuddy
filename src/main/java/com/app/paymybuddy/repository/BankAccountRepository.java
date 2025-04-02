package com.app.paymybuddy.repository;

import com.app.paymybuddy.model.BankAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository
  extends JpaRepository<BankAccount, Integer> {
  /**
   * Find a bank account by user id.
   *
   * @param userId the user id
   * @return the bank account
   */
  @Query("SELECT b FROM BankAccount b WHERE b.user.id = :userId")
  Optional<BankAccount> findByUserId(Integer userId);
}
