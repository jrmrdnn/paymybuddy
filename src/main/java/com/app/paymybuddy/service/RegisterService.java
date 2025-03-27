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

  /**
   * Save a new user.
   * @param userDto the user to save.
   */
  @Transactional
  public void saveUser(UserRegisterDto userDto) {
    validateEmailUniqueness(userDto.getEmail());

    User user = new User();
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

    userRepository.save(user);
  }

  /**
   * Check if the email is already used.
   * @param email the email to check.
   */
  private void validateEmailUniqueness(String email) {
    userRepository
      .findByEmailAndDeletedAtIsNull(email)
      .ifPresent(u -> {
        throw new EmailAlreadyUsedException(
          "Email : " + email + " est déjà utilisé !!"
        );
      });
  }
}
