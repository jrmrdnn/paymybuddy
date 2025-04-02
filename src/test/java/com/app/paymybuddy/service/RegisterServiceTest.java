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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
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

  @Mock
  private BankAccountService bankAccountService;

  private UserRegisterDto userDto;

  @BeforeEach
  void setUp() {
    userDto = new UserRegisterDto();
    userDto.setUsername("user");
    userDto.setEmail("user@example.com");
    userDto.setPassword("password");
  }

  @Test
  void saveUser_shouldCreateBankAccount_afterSavingUser() {
    when(
      userRepository.findByEmailAndDeletedAtIsNull(userDto.getEmail())
    ).thenReturn(Optional.empty());
    when(passwordEncoder.encode(userDto.getPassword())).thenReturn(
      "encodedPassword"
    );

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    registerService.saveUser(userDto);

    verify(userRepository).save(userCaptor.capture());

    verify(bankAccountService).createBankAccount(userCaptor.getValue());
  }

  @Test
  void saveUser_shouldNotCreateBankAccount_whenEmailIsAlreadyUsed() {
    when(
      userRepository.findByEmailAndDeletedAtIsNull(userDto.getEmail())
    ).thenReturn(Optional.of(new User()));

    assertThrows(EmailAlreadyUsedException.class, () ->
      registerService.saveUser(userDto)
    );

    verify(bankAccountService, never()).createBankAccount(any());
  }

  @Test
  void saveUser_shouldExecuteInCorrectOrder() {
    when(
      userRepository.findByEmailAndDeletedAtIsNull(userDto.getEmail())
    ).thenReturn(Optional.empty());
    when(passwordEncoder.encode(userDto.getPassword())).thenReturn(
      "encodedPassword"
    );

    registerService.saveUser(userDto);

    InOrder inOrder = inOrder(userRepository, bankAccountService);
    inOrder.verify(userRepository).save(any(User.class));
    inOrder.verify(bankAccountService).createBankAccount(any(User.class));
  }
}
