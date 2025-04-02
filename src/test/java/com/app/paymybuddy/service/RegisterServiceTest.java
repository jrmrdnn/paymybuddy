package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private RegisterService registerService;

  private UserRegisterDto userDto;

  @BeforeEach
  void setUp() {
    userDto = new UserRegisterDto();
    userDto.setUsername("user");
    userDto.setEmail("user@example.com");
    userDto.setPassword("password");
  }

  @Test
  void saveUser_shouldSaveUser_whenEmailIsUnique() {
    when(
      userRepository.findByEmailAndDeletedAtIsNull(userDto.getEmail())
    ).thenReturn(Optional.empty());
    when(passwordEncoder.encode(userDto.getPassword())).thenReturn(
      "encodedPassword"
    );

    registerService.saveUser(userDto);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void saveUser_shouldThrowException_whenEmailIsAlreadyUsed() {
    when(
      userRepository.findByEmailAndDeletedAtIsNull(userDto.getEmail())
    ).thenReturn(Optional.of(new User()));

    assertThrows(EmailAlreadyUsedException.class, () ->
      registerService.saveUser(userDto)
    );
    verify(userRepository, never()).save(any(User.class));
  }
}
