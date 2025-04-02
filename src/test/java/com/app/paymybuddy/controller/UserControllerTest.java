package com.app.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.UserUpdateDto;
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

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new UserNotFoundException())
      .when(userService)
      .updateUser(authentication, userUpdateDto);

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(bindingResult).rejectValue(
      "email",
      "error.userUpdateDto",
      "Utilisateur non trouvé"
    );
  }

  @Test
  public void updateProfil_ShouldHandleIllegalArgumentException() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(
      new IllegalArgumentException(
        "Le mot de passe doit contenir au moins une lettre minuscule, une lettre majuscule, un chiffre et un caractère spécial."
      )
    )
      .when(userService)
      .updateUser(authentication, userUpdateDto);

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(bindingResult).rejectValue(
      "password",
      "error.userUpdateDto",
      "Le mot de passe doit contenir au moins une lettre minuscule, une lettre majuscule, un chiffre et un caractère spécial."
    );
  }

  @Test
  public void updateProfil_ShouldHandleGenericException() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new RuntimeException("Unexpected error"))
      .when(userService)
      .updateUser(authentication, userUpdateDto);

    String viewName = userController.updateProfil(
      userUpdateDto,
      bindingResult,
      redirectAttributes,
      authentication,
      model
    );

    assertEquals("profil", viewName);
    verify(bindingResult).rejectValue(
      "username",
      "error.userUpdateDto",
      "Une erreur c'est produite"
    );
  }
}
