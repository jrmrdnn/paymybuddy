package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserUpdateDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private Authentication authentication;

  @Mock
  private CustomUserDetails customUserDetails;

  @InjectMocks
  private UserService userService;

  private User currentUser;
  private UserUpdateDto userUpdate;
  private final Integer userId = 1;

  @BeforeEach
  void setUp() {
    currentUser = new User();
    currentUser.setId(userId);
    currentUser.setUsername("user");
    currentUser.setEmail("test@example.com");
    currentUser.setPassword("encodedPassword");

    userUpdate = new UserUpdateDto();
    userUpdate.setUsername("updatedUser");
    userUpdate.setEmail("updated@example.com");

    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(customUserDetails.getUserId()).thenReturn(userId);
  }

  @Test
  void findById_whenUserExists_shouldReturnUser() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(currentUser)
    );

    User result = userService.findById(authentication);

    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("user", result.getUsername());
    assertEquals("test@example.com", result.getEmail());
  }

  @Test
  void findById_whenUserNotFound_shouldThrowException() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.empty()
    );

    UserNotFoundException exception = assertThrows(
      UserNotFoundException.class,
      () -> userService.findById(authentication)
    );

    assertEquals("Utilisateur non trouvé", exception.getMessage());
  }

  @Test
  void updateUser_withSameEmail_shouldNotCheckEmailUniqueness() {
    userUpdate.setEmail("test@example.com");
    when(userRepository.findByIdAndDeletedAtIsNull(1)).thenReturn(
      Optional.of(currentUser)
    );

    userService.updateUser(authentication, userUpdate);

    verify(userRepository, never()).findByEmail(anyString());
    verify(userRepository).save(currentUser);
    assertEquals("updatedUser", currentUser.getUsername());
    assertEquals("test@example.com", currentUser.getEmail());
  }

  @Test
  void updateUser_withNewUniqueEmail_shouldUpdateUser() {
    userUpdate.setEmail("new.email@example.com");
    when(userRepository.findByIdAndDeletedAtIsNull(1)).thenReturn(
      Optional.of(currentUser)
    );
    when(userRepository.findByEmail("new.email@example.com")).thenReturn(
      Optional.empty()
    );

    userService.updateUser(authentication, userUpdate);

    verify(userRepository).findByEmail("new.email@example.com");
    verify(userRepository).save(currentUser);
    assertEquals("updatedUser", currentUser.getUsername());
    assertEquals("new.email@example.com", currentUser.getEmail());
  }

  @Test
  void updateUser_withExistingEmail_shouldThrowException() {
    userUpdate.setEmail("existing@example.com");
    User existingUser = new User();
    existingUser.setId(2);
    existingUser.setEmail("existing@example.com");

    when(userRepository.findByIdAndDeletedAtIsNull(1)).thenReturn(
      Optional.of(currentUser)
    );
    when(userRepository.findByEmail("existing@example.com")).thenReturn(
      Optional.of(existingUser)
    );

    EmailAlreadyUsedException exception = assertThrows(
      EmailAlreadyUsedException.class,
      () -> userService.updateUser(authentication, userUpdate)
    );

    assertEquals(
      "L'email est déjà utilisé par un autre utilisateur.",
      exception.getMessage()
    );
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_userNotFound_shouldThrowException() {
    when(userRepository.findByIdAndDeletedAtIsNull(1)).thenReturn(
      Optional.empty()
    );

    assertThrows(UserNotFoundException.class, () ->
      userService.updateUser(authentication, userUpdate)
    );

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_withValidPassword_shouldEncodeAndUpdatePassword() {
    userUpdate.setPassword("Valid1Password!");
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(currentUser)
    );
    when(passwordEncoder.encode("Valid1Password!")).thenReturn(
      "encodedNewPassword"
    );

    userService.updateUser(authentication, userUpdate);

    verify(passwordEncoder).encode("Valid1Password!");
    verify(userRepository).save(currentUser);
    assertEquals("encodedNewPassword", currentUser.getPassword());
  }

  @Test
  void updateUser_withInvalidPassword_shouldThrowException() {
    userUpdate.setPassword("invalidpassword");
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(currentUser)
    );

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> userService.updateUser(authentication, userUpdate)
    );

    assertEquals(
      "Le mot de passe doit contenir au moins une lettre minuscule, une lettre majuscule, un chiffre et un caractère spécial.",
      exception.getMessage()
    );
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_withNullPassword_shouldNotUpdatePassword() {
    userUpdate.setPassword(null);
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(currentUser)
    );

    userService.updateUser(authentication, userUpdate);

    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository).save(currentUser);
    assertEquals("encodedPassword", currentUser.getPassword());
  }

  @Test
  void updateUser_withEmptyPassword_shouldNotUpdatePassword() {
    userUpdate.setPassword("");
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(currentUser)
    );

    userService.updateUser(authentication, userUpdate);

    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository).save(currentUser);
    assertEquals("encodedPassword", currentUser.getPassword());
  }
}
