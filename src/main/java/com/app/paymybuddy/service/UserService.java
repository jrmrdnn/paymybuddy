package com.app.paymybuddy.service;

import com.app.paymybuddy.constants.RegexpConstants;
import com.app.paymybuddy.dto.request.UserUpdateDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Find a user by id.
   * @param authentication the authentication object
   * @return User
   */
  public User findById(Authentication authentication) {
    return userRepository
      .findByIdAndDeletedAtIsNull(
        ((CustomUserDetails) authentication.getPrincipal()).getUserId()
      )
      .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
  }

  /**
   * Update a user.
   * @param authentication the authentication object
   * @param userUpdateDto the user to update
   */
  @Transactional
  public void updateUser(
    Authentication authentication,
    UserUpdateDto userUpdateDto
  ) {
    User user = userRepository
      .findByIdAndDeletedAtIsNull(
        ((CustomUserDetails) authentication.getPrincipal()).getUserId()
      )
      .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

    user.setUsername(userUpdateDto.getUsername());

    if (!Objects.equals(userUpdateDto.getEmail(), user.getEmail())) {
      if (userRepository.findByEmail(userUpdateDto.getEmail()).isPresent()) {
        throw new EmailAlreadyUsedException(
          "L'email est déjà utilisé par un autre utilisateur."
        );
      }
    }

    user.setEmail(userUpdateDto.getEmail());

    if (
      userUpdateDto.getPassword() != null &&
      !userUpdateDto.getPassword().isEmpty()
    ) {
      String password = userUpdateDto.getPassword();
      if (!password.matches(RegexpConstants.PASSWORD_REGEXP)) {
        throw new IllegalArgumentException(
          "Le mot de passe doit contenir au moins une lettre minuscule, une lettre majuscule, un chiffre et un caractère spécial."
        );
      }
      user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
    }

    userRepository.save(user);
  }
}
