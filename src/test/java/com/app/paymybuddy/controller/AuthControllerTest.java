package com.app.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private RegisterService registerService;

  @Mock
  private Model model;

  @Mock
  private BindingResult bindingResult;

  @InjectMocks
  private AuthController authController;

  private UserRegisterDto userRegisterDto;

  @BeforeEach
  void setUp() {
    userRegisterDto = new UserRegisterDto();
  }

  @Test
  void testShowLogin() {
    String viewName = authController.showLogin();
    assertEquals("login", viewName);
  }

  @Test
  void testShowRegister() {
    String viewName = authController.showRegister(model);
    assertEquals("register", viewName);
    verify(model, times(1)).addAttribute(
      eq("userRegisterDto"),
      any(UserRegisterDto.class)
    );
  }

  @Test
  void testRegisterUser_Success() throws EmailAlreadyUsedException {
    when(bindingResult.hasErrors()).thenReturn(false);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult
    );

    assertEquals("redirect:/login", viewName);
    verify(registerService, times(1)).saveUser(userRegisterDto);
  }

  @Test
  void testRegisterUser_ValidationErrors() {
    when(bindingResult.hasErrors()).thenReturn(true);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult
    );

    assertEquals("register", viewName);
    verify(registerService, never()).saveUser(any());
  }

  @Test
  void testRegisterUser_EmailAlreadyUsedException()
    throws EmailAlreadyUsedException {
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new EmailAlreadyUsedException("Email is already in use"))
      .when(registerService)
      .saveUser(userRegisterDto);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult
    );

    assertEquals("register", viewName);
    verify(bindingResult, times(1)).rejectValue(
      "email",
      "error.userRegisterDto",
      "Email is already in use"
    );
  }

  @Test
  void testRegisterUser_GenericException() throws EmailAlreadyUsedException {
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new RuntimeException("Unexpected error"))
      .when(registerService)
      .saveUser(userRegisterDto);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult
    );

    assertEquals("register", viewName);
    verify(bindingResult, times(1)).rejectValue(
      "email",
      "error.userRegisterDto",
      "Une erreur c'est produite"
    );
  }
}
