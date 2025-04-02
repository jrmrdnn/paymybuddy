package com.app.paymybuddy.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.app.paymybuddy.model.User;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class CustomUserDetailsTest {

  private User mockUser;
  private CustomUserDetails customUserDetails;
  private final Integer USER_ID = 1;
  private final String USER_EMAIL = "test@example.com";
  private final String USER_PASSWORD = "password123";

  @BeforeEach
  void setUp() {
    mockUser = mock(User.class);
    when(mockUser.getId()).thenReturn(USER_ID);
    when(mockUser.getEmail()).thenReturn(USER_EMAIL);
    when(mockUser.getPassword()).thenReturn(USER_PASSWORD);

    customUserDetails = new CustomUserDetails(mockUser);
  }

  @Test
  void getUserId_ShouldReturnUserId() {
    assertEquals(USER_ID, customUserDetails.getUserId());
  }

  @Test
  void getAuthorities_ShouldReturnRoleUser() {
    Collection<? extends GrantedAuthority> authorities =
      customUserDetails.getAuthorities();

    assertEquals(1, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
  }

  @Test
  void getPassword_ShouldReturnUserPassword() {
    assertEquals(USER_PASSWORD, customUserDetails.getPassword());
  }

  @Test
  void getUsername_ShouldReturnUserEmail() {
    assertEquals(USER_EMAIL, customUserDetails.getUsername());
  }

  @Test
  void isAccountNonExpired_ShouldReturnTrue() {
    assertTrue(customUserDetails.isAccountNonExpired());
  }

  @Test
  void isAccountNonLocked_ShouldReturnTrue() {
    assertTrue(customUserDetails.isAccountNonLocked());
  }

  @Test
  void isCredentialsNonExpired_ShouldReturnTrue() {
    assertTrue(customUserDetails.isCredentialsNonExpired());
  }

  @Test
  void isEnabled_ShouldReturnTrue() {
    assertTrue(customUserDetails.isEnabled());
  }
}
