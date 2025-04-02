package com.app.paymybuddy.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationTest {

  private Relation relation;
  private User user;
  private User relationUser;

  @BeforeEach
  public void setUp() {
    relation = new Relation();
    user = new User();
    user.setId(1);
    user.setEmail("user@example.com");

    relationUser = new User();
    relationUser.setId(2);
    relationUser.setEmail("friend@example.com");
  }

  @Test
  public void testConstructor() {
    assertNotNull(relation);
    assertNull(relation.getId());
    assertNull(relation.getUser());
    assertNull(relation.getRelationUser());
    assertNull(relation.getCreatedAt());
  }

  @Test
  public void testGettersAndSetters() {
    relation.setId(1);
    relation.setUser(user);
    relation.setRelationUser(relationUser);
    LocalDateTime now = LocalDateTime.now();
    relation.setCreatedAt(now);

    assertEquals(1, relation.getId());
    assertEquals(user, relation.getUser());
    assertEquals(relationUser, relation.getRelationUser());
    assertEquals(now, relation.getCreatedAt());
  }

  @Test
  public void testOnCreate() throws Exception {
    Method onCreate = Relation.class.getDeclaredMethod("onCreate");
    onCreate.setAccessible(true);
    onCreate.invoke(relation);

    assertNotNull(relation.getCreatedAt());
    assertTrue(
      relation.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(2))
    );
    assertTrue(
      relation.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(2))
    );
  }

  @Test
  public void testEqualsAndHashCode() {
    Relation relation1 = new Relation();
    relation1.setId(1);
    relation1.setUser(user);
    relation1.setRelationUser(relationUser);

    Relation relation2 = new Relation();
    relation2.setId(1);
    relation2.setUser(relationUser);
    relation2.setRelationUser(relationUser);

    assertEquals(relation1, relation2);
    assertEquals(relation1.hashCode(), relation2.hashCode());

    relation2.setId(2);
    assertNotEquals(relation1, relation2);
    assertNotEquals(relation1.hashCode(), relation2.hashCode());
  }

  @Test
  public void testToString() {
    relation.setId(1);
    relation.setUser(user);
    relation.setRelationUser(relationUser);

    String toString = relation.toString();

    assertFalse(toString.contains("user@example.com"));

    assertTrue(toString.contains("relationUser"));
    assertTrue(toString.contains("friend@example.com"));
    assertTrue(toString.contains("id=1"));
  }
}
