package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserUpdateDto;
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

  private User testUser;
  private UserUpdateDto updateDto;
  private final Integer userId = 1;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(userId);
    testUser.setUsername("testUser");
    testUser.setEmail("test@example.com");
    testUser.setPassword("encodedPassword");

    updateDto = new UserUpdateDto();
    updateDto.setUsername("updatedUser");
    updateDto.setEmail("updated@example.com");

    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(customUserDetails.getUserId()).thenReturn(userId);
  }

  @Test
  void findById_ShouldReturnUser_WhenUserExists() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(testUser)
    );

    User result = userService.findById(authentication);

    assertEquals(testUser, result);
    verify(userRepository).findByIdAndDeletedAtIsNull(userId);
  }

  @Test
  void findById_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.empty()
    );

    assertThrows(UserNotFoundException.class, () ->
      userService.findById(authentication)
    );
  }

  @Test
  void updateUser_ShouldUpdateUserInfo_WhenUserExists() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(testUser)
    );

    userService.updateUser(authentication, updateDto);

    assertEquals(updateDto.getUsername(), testUser.getUsername());
    assertEquals(updateDto.getEmail(), testUser.getEmail());
    verify(userRepository).save(testUser);
  }

  @Test
  void updateUser_ShouldUpdatePassword_WhenPasswordProvided() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(testUser)
    );
    updateDto.setPassword("NewPass1!");
    when(passwordEncoder.encode(updateDto.getPassword())).thenReturn(
      "newEncodedPassword"
    );

    userService.updateUser(authentication, updateDto);

    assertEquals("newEncodedPassword", testUser.getPassword());
    verify(passwordEncoder).encode(updateDto.getPassword());
  }

  @Test
  void updateUser_ShouldThrowException_WhenPasswordInvalid() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(testUser)
    );
    updateDto.setPassword("invalid");

    assertThrows(IllegalArgumentException.class, () ->
      userService.updateUser(authentication, updateDto)
    );
  }

  @Test
  void updateUser_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.empty()
    );

    assertThrows(UserNotFoundException.class, () ->
      userService.updateUser(authentication, updateDto)
    );
  }

  @Test
  void updateUser_ShouldNotUpdatePassword_WhenPasswordIsEmpty() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(testUser)
    );
    updateDto.setPassword("");

    userService.updateUser(authentication, updateDto);

    assertEquals("encodedPassword", testUser.getPassword());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository).save(testUser);
  }

  @Test
  void updateUser_ShouldNotUpdatePassword_WhenPasswordIsNull() {
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(
      Optional.of(testUser)
    );
    updateDto.setPassword(null);

    userService.updateUser(authentication, updateDto);

    assertEquals("encodedPassword", testUser.getPassword());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository).save(testUser);
  }
}
