package com.app.paymybuddy.config;

import com.app.paymybuddy.security.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

  private CustomUserDetailsService customUserDetailsService;

  private static final String[] PUBLIC_PATHS = {
    "/",
    "/login",
    "/register",
    "/css/**",
    "/js/**",
    "/img/**",
  };
  private static final String LOGIN_PATH = "/login";
  private static final String LOGOUT_PATH = "/logout";
  private static final String SUCCESS_PATH = "/transfer";

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
    throws Exception {
    http
      .authorizeHttpRequests(requests ->
        requests
          .requestMatchers(PUBLIC_PATHS)
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .formLogin(form ->
        form
          .loginPage(LOGIN_PATH)
          .usernameParameter("email")
          .passwordParameter("password")
          .defaultSuccessUrl(SUCCESS_PATH, true)
          .permitAll()
      )
      .logout(logout ->
        logout
          .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PATH))
          .logoutSuccessUrl(LOGIN_PATH + "?logout")
          .invalidateHttpSession(true)
          .deleteCookies("SESSION_PAYMYBUDDY")
          .permitAll()
      )
      .userDetailsService(customUserDetailsService);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
