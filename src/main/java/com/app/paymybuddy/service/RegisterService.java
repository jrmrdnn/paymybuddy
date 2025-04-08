package com.app.paymybuddy.service;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegisterService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final BankAccountService bankAccountService;

  /**
   * Save a new user.
   * @param userDto the user to save.
   */
  @Transactional
  public void saveUser(UserRegisterDto userDto) {
    // 1. Check if the user already exists
    validateEmailUniqueness(userDto.getEmail());

    // 2. Create the user
    User user = new User();
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

    // 3. Set the role to USER
    userRepository.save(user);

    // 4. Create the bank account
    bankAccountService.createBankAccount(user);
  }

  /**
   * Check if the email is already used.
   * @param email the email to check.
   */
  private void validateEmailUniqueness(String email) {
    userRepository
      .findByEmail(email)
      .ifPresent(u -> {
        throw new EmailAlreadyUsedException("Email est déjà utilisé !!");
      });
  }
}
