package com.app.paymybuddy.service;

import com.app.paymybuddy.repository.UserRepository;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    return userRepository
      .findByEmailAndDeletedAtIsNull(email)
      .map(u -> new User(u.getEmail(), u.getPassword(), new ArrayList<>()))
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé")
      );
  }
}
