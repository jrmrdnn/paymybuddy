package com.app.paymybuddy.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setId(1);
    user.setUsername("testUser");
    user.setEmail("test@example.com");
    user.setPassword("password123");
  }

  @Test
  public void testUserProperties() {
    assertEquals(1, user.getId());
    assertEquals("testUser", user.getUsername());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("password123", user.getPassword());
  }

  @Test
  public void testPrePersist() {
    assertNull(user.getCreatedAt());
    user.onCreate();
    assertNotNull(user.getCreatedAt());
    assertTrue(
      user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1))
    );
    assertTrue(
      user.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))
    );
  }

  @Test
  public void testPreUpdate() {
    assertNull(user.getUpdatedAt());
    user.onUpdate();
    assertNotNull(user.getUpdatedAt());
    assertTrue(
      user.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1))
    );
    assertTrue(
      user.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1))
    );
  }

  @Test
  public void testRelationships() {
    HashSet<Relation> myRelations = new HashSet<>();
    HashSet<Relation> relatedToMe = new HashSet<>();

    user.setMyRelations(myRelations);
    user.setRelatedToMe(relatedToMe);

    assertEquals(myRelations, user.getMyRelations());
    assertEquals(relatedToMe, user.getRelatedToMe());
    assertTrue(user.getMyRelations().isEmpty());
    assertTrue(user.getRelatedToMe().isEmpty());
  }

  @Test
  public void testDeletedAt() {
    assertNull(user.getDeletedAt());
    LocalDateTime now = LocalDateTime.now();
    user.setDeletedAt(now);
    assertEquals(now, user.getDeletedAt());
  }
}
