package com.app.paymybuddy.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "sender_user_id", updatable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver_user_id", updatable = false)
  private User receiver;

  @Column(nullable = false, length = 50, updatable = false)
  private String description;

  @Column(
    nullable = false,
    updatable = false,
    columnDefinition = "DECIMAL(10,2)"
  )
  private double amount;

  @Column(
    name = "created_at",
    nullable = false,
    updatable = false,
    columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP"
  )
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
