package com.app.paymybuddy.security;

import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Load user by email.
   * @param email
   */
  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    User user = userRepository
      .findByEmailAndDeletedAtIsNull(email)
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé")
      );

    return new CustomUserDetails(user);
  }
}
