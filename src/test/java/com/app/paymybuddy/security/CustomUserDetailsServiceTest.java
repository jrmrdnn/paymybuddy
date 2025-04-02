package com.app.paymybuddy.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomUserDetailsService customUserDetailsService;

  private final String TEST_EMAIL = "test@example.com";
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setEmail(TEST_EMAIL);
  }

  @Test
  void loadUserByUsername_WhenUserExists_ShouldReturnCustomUserDetails() {
    when(userRepository.findByEmailAndDeletedAtIsNull(TEST_EMAIL)).thenReturn(
      Optional.of(testUser)
    );

    UserDetails userDetails = customUserDetailsService.loadUserByUsername(
      TEST_EMAIL
    );

    assertNotNull(userDetails);
    assertTrue(userDetails instanceof CustomUserDetails);
    assertEquals(TEST_EMAIL, userDetails.getUsername());
    verify(userRepository, times(1)).findByEmailAndDeletedAtIsNull(TEST_EMAIL);
  }

  @Test
  void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
    when(userRepository.findByEmailAndDeletedAtIsNull(TEST_EMAIL)).thenReturn(
      Optional.empty()
    );

    UsernameNotFoundException exception = assertThrows(
      UsernameNotFoundException.class,
      () -> {
        customUserDetailsService.loadUserByUsername(TEST_EMAIL);
      }
    );

    assertEquals("Utilisateur non trouvé", exception.getMessage());
    verify(userRepository, times(1)).findByEmailAndDeletedAtIsNull(TEST_EMAIL);
  }
}
