package com.app.paymybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

  @Id
  @Column(name = "user_id")
  private Integer userId;

  @MapsId
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(
    name = "balance",
    nullable = false,
    columnDefinition = "DECIMAL(10,2)"
  )
  private Double balance;

  @Column(name = "account_number", unique = true, length = 50)
  private String accountNumber;

  @Column(name = "iban", unique = true, length = 34)
  private String iban;

  @Column(name = "bic", length = 11)
  private String bic;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
