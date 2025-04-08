package com.app.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserRegisterDto;
import com.app.paymybuddy.exception.EmailAlreadyUsedException;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Mock
  private RegisterService registerService;

  @Mock
  private Model model;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private RedirectAttributes redirectAttributes;

  @Spy
  private HandleException handleException = new HandleException();

  @InjectMocks
  private AuthController authController;

  private UserRegisterDto userRegisterDto;

  @Test
  public void testShowLogin() {
    String viewName = authController.showLogin(redirectAttributes);

    assertEquals("login", viewName);
  }

  @Test
  public void testShowRegister() {
    String viewName = authController.showRegister(model);

    assertEquals("register", viewName);
    verify(model, times(1)).addAttribute(
      eq("userRegisterDto"),
      any(UserRegisterDto.class)
    );
  }

  @Test
  public void testRegisterUser_Success() throws EmailAlreadyUsedException {
    when(bindingResult.hasErrors()).thenReturn(false);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult,
      redirectAttributes
    );

    assertEquals("redirect:/login", viewName);
    verify(registerService, times(1)).saveUser(userRegisterDto);
  }

  @Test
  public void testRegisterUser_ValidationErrors() {
    when(bindingResult.hasErrors()).thenReturn(true);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult,
      redirectAttributes
    );

    assertEquals("register", viewName);
    verify(registerService, never()).saveUser(any());
  }

  @Test
  public void testRegisterUser_EmailAlreadyUsedException()
    throws EmailAlreadyUsedException {
    EmailAlreadyUsedException exception = new EmailAlreadyUsedException(
      "Email is already in use"
    );

    String result = handleException.exceptionAuth(exception, bindingResult);

    assertEquals("register", result);

    verify(bindingResult).rejectValue(
      eq("email"),
      eq("error.userRegisterDto"),
      eq("Email is already in use")
    );
  }

  @Test
  public void testRegisterUser_GenericException()
    throws EmailAlreadyUsedException {
    RuntimeException exception = new RuntimeException("Une erreur générique");

    String result = handleException.exceptionAuth(exception, bindingResult);

    assertEquals("register", result);

    verify(bindingResult).rejectValue(
      eq("email"),
      eq("error.userRegisterDto"),
      eq("Une erreur c'est produite")
    );
  }

  @Test
  public void testRegisterUser_EmailAlreadyUsedException_WithRealHandling()
    throws EmailAlreadyUsedException {
    when(bindingResult.hasErrors()).thenReturn(false);
    EmailAlreadyUsedException exception = new EmailAlreadyUsedException(
      "Email is already in use"
    );

    doThrow(exception).when(registerService).saveUser(userRegisterDto);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult,
      redirectAttributes
    );

    assertEquals("register", viewName);
    verify(bindingResult).rejectValue(
      eq("email"),
      eq("error.userRegisterDto"),
      eq("Email is already in use")
    );
  }

  @Test
  public void testRegisterUser_GenericException_WithRealHandling()
    throws EmailAlreadyUsedException {
    when(bindingResult.hasErrors()).thenReturn(false);
    RuntimeException exception = new RuntimeException("Unexpected error");

    doThrow(exception).when(registerService).saveUser(userRegisterDto);

    String viewName = authController.registerUser(
      userRegisterDto,
      bindingResult,
      redirectAttributes
    );

    assertEquals("register", viewName);
    verify(bindingResult).rejectValue(
      eq("email"),
      eq("error.userRegisterDto"),
      eq("Une erreur c'est produite")
    );
  }
}
