package com.app.paymybuddy.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTest {

  private Transaction transaction;
  private User sender;
  private User receiver;

  @BeforeEach
  public void setUp() {
    transaction = new Transaction();
    sender = new User();
    sender.setId(1);
    receiver = new User();
    receiver.setId(2);
  }

  @Test
  public void testSetAndGetId() {
    Integer id = 1;
    transaction.setId(id);
    assertEquals(id, transaction.getId());
  }

  @Test
  public void testSetAndGetSender() {
    transaction.setSender(sender);
    assertEquals(sender, transaction.getSender());
  }

  @Test
  public void testSetAndGetReceiver() {
    transaction.setReceiver(receiver);
    assertEquals(receiver, transaction.getReceiver());
  }

  @Test
  public void testSetAndGetDescription() {
    String description = "Payment for lunch";
    transaction.setDescription(description);
    assertEquals(description, transaction.getDescription());
  }

  @Test
  public void testSetAndGetAmount() {
    double amount = 50.75;
    transaction.setAmount(amount);
    assertEquals(amount, transaction.getAmount(), 0.001);
  }

  @Test
  public void testSetAndGetCreatedAt() {
    LocalDateTime now = LocalDateTime.now();
    transaction.setCreatedAt(now);
    assertEquals(now, transaction.getCreatedAt());
  }

  @Test
  public void testOnCreate() {
    assertNull(transaction.getCreatedAt());
    transaction.onCreate();
    assertNotNull(transaction.getCreatedAt());

    LocalDateTime now = LocalDateTime.now();
    assertTrue(
      java.time.Duration.between(transaction.getCreatedAt(), now).getSeconds() <
      1
    );
  }

  @Test
  public void testEqualsAndHashCode() {
    Transaction transaction1 = new Transaction();
    transaction1.setId(1);
    transaction1.setSender(sender);
    transaction1.setReceiver(receiver);
    transaction1.setDescription("Test payment");
    transaction1.setAmount(100.0);

    Transaction transaction2 = new Transaction();
    transaction2.setId(1);
    transaction2.setSender(sender);
    transaction2.setReceiver(receiver);
    transaction2.setDescription("Test payment");
    transaction2.setAmount(100.0);

    assertEquals(transaction1, transaction2);
    assertEquals(transaction1.hashCode(), transaction2.hashCode());

    transaction2.setAmount(200.0);
    assertNotEquals(transaction1, transaction2);
  }

  @Test
  public void testToString() {
    transaction.setId(1);
    transaction.setSender(sender);
    transaction.setReceiver(receiver);
    transaction.setDescription("Test");
    transaction.setAmount(50.0);

    String toString = transaction.toString();

    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("description=Test"));
    assertTrue(toString.contains("amount=50.0"));
  }
}
