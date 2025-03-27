package com.app.paymybuddy.config;

import com.app.paymybuddy.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
    this.customUserDetailsService = customUserDetailsService;
  }

  private static final String[] PUBLIC_PATHS = {
    "/",
    "/login",
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
      );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }
}
