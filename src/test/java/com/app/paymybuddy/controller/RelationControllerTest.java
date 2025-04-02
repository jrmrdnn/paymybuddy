package com.app.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.RelationDto;
import com.app.paymybuddy.exception.RelationAlreadyExistsException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.service.RelationService;
import org.junit.jupiter.api.BeforeEach;
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
class RelationControllerTest {

  @Mock
  private RelationService relationService;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectAttributes;

  @Mock
  private Authentication authentication;

  @Mock
  private BindingResult bindingResult;

  @InjectMocks
  private RelationController relationController;

  private RelationDto relationDto;

  @BeforeEach
  void setUp() {
    relationDto = new RelationDto();
  }

  @Test
  void testShowRelation() {
    String viewName = relationController.showRelation(
      redirectAttributes,
      model
    );

    verify(model, times(1)).addAttribute(
      eq("relationDto"),
      any(RelationDto.class)
    );
    assertEquals("relation", viewName);
  }

  @Test
  void testAddRelation_Success() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(relationService, times(1)).saveRelation(authentication, relationDto);
    verify(redirectAttributes, times(1)).addFlashAttribute(
      "successMessage",
      "Relation ajoutée avec succès !!"
    );
    assertEquals("redirect:/transfer", viewName);
  }

  @Test
  void testAddRelation_BindingErrors() {
    when(bindingResult.hasErrors()).thenReturn(true);

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    assertEquals("relation", viewName);
    verifyNoInteractions(relationService);
  }

  @Test
  void testAddRelation_RelationAlreadyExistsException() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new RelationAlreadyExistsException("Relation already exists"))
      .when(relationService)
      .saveRelation(authentication, relationDto);

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(bindingResult, times(1)).rejectValue(
      "email",
      "error.relation",
      "Relation already exists"
    );
    assertEquals("relation", viewName);
  }

  @Test
  void testAddRelation_UserNotFoundException() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new UserNotFoundException("User not found"))
      .when(relationService)
      .saveRelation(authentication, relationDto);

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(bindingResult, times(1)).rejectValue(
      "email",
      "error.relation",
      "User not found"
    );
    assertEquals("relation", viewName);
  }

  @Test
  void testAddRelation_GenericException() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new RuntimeException("Unexpected error"))
      .when(relationService)
      .saveRelation(authentication, relationDto);

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(redirectAttributes, times(1)).addFlashAttribute(
      "errorMessage",
      "Une erreur c'est produite !!"
    );
    assertEquals("redirect:/relation", viewName);
  }
}
