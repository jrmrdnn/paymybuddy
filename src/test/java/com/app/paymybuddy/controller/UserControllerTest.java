package com.app.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserUpdateDto;
import com.app.paymybuddy.exception.HandleException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private Authentication authentication;

  @Mock
  private Model model;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private HandleException handleException;

  @Mock
  private RedirectAttributes redirectAttributes;

  @InjectMocks
  private UserController userController;

  @Test
  public void showProfil_ShouldReturnProfilView() {
    when(userService.findById(authentication)).thenReturn(new User());

    String viewName = userController.showProfil(
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(model).addAttribute(eq("userUpdateDto"), any(User.class));
  }

  @Test
  public void updateProfil_ShouldRedirectToTransfer_WhenUpdateIsSuccessful() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    when(bindingResult.hasErrors()).thenReturn(false);

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("redirect:/transfer", viewName);
    verify(userService).updateUser(authentication, userUpdateDto);
    verify(redirectAttributes).addFlashAttribute(
      "successMessage",
      "Profil mis à jour avec succès !!"
    );
  }

  @Test
  public void updateProfil_ShouldReturnProfilView_WhenValidationFails() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    when(bindingResult.hasErrors()).thenReturn(true);

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verifyNoInteractions(userService);
  }

  @Test
  public void updateProfil_ShouldHandleUserNotFoundException() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();
    Exception exception = new UserNotFoundException("User not found");

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(exception)
      .when(userService)
      .updateUser(authentication, userUpdateDto);

    when(
      handleException.exceptionUser(
        any(UserNotFoundException.class),
        eq(bindingResult)
      )
    ).thenReturn("profil");

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(handleException).exceptionUser(
      any(UserNotFoundException.class),
      eq(bindingResult)
    );
  }

  @Test
  public void updateProfil_ShouldHandleIllegalArgumentException() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();
    Exception exception = new IllegalArgumentException("Invalid argument");

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(exception)
      .when(userService)
      .updateUser(authentication, userUpdateDto);

    when(
      handleException.exceptionUser(
        any(IllegalArgumentException.class),
        eq(bindingResult)
      )
    ).thenReturn("profil");

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(handleException).exceptionUser(
      any(IllegalArgumentException.class),
      eq(bindingResult)
    );
  }

  @Test
  public void updateProfil_ShouldHandleGenericException() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();
    Exception exception = new RuntimeException("Unexpected error");

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(exception)
      .when(userService)
      .updateUser(authentication, userUpdateDto);

    when(
      handleException.exceptionUser(
        any(RuntimeException.class),
        eq(bindingResult)
      )
    ).thenReturn("profil");

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(handleException).exceptionUser(
      any(RuntimeException.class),
      eq(bindingResult)
    );
  }
}
