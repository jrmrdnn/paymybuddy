package com.app.paymybuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(
  name = "users",
  uniqueConstraints = { @UniqueConstraint(columnNames = "email") }
)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @ManyToMany
  @JoinTable(
    name = "relations",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "relation_user_id")
  )
  private List<User> relations;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime deletedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
