package com.app.paymybuddy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.RelationDto;
import com.app.paymybuddy.exception.HandleException;
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

  @Mock
  private HandleException handleException;

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

    verify(relationService, times(1)).saveRelation(
      relationDto,
      authentication,
      redirectAttributes
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
    RelationAlreadyExistsException exception =
      new RelationAlreadyExistsException("Relation already exists");
    doThrow(exception)
      .when(relationService)
      .saveRelation(relationDto, authentication, redirectAttributes);

    when(
      handleException.exceptionRelation(
        any(RelationAlreadyExistsException.class),
        eq(bindingResult)
      )
    ).thenReturn("relation");

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(handleException).exceptionRelation(eq(exception), eq(bindingResult));
    assertEquals("relation", viewName);
  }

  @Test
  void testAddRelation_UserNotFoundException() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);
    UserNotFoundException exception = new UserNotFoundException(
      "User not found"
    );
    doThrow(exception)
      .when(relationService)
      .saveRelation(relationDto, authentication, redirectAttributes);

    when(
      handleException.exceptionRelation(
        any(UserNotFoundException.class),
        eq(bindingResult)
      )
    ).thenReturn("relation");

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(handleException).exceptionRelation(eq(exception), eq(bindingResult));
    assertEquals("relation", viewName);
  }

  @Test
  void testAddRelation_GenericException() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);
    RuntimeException exception = new RuntimeException("Unexpected error");
    doThrow(exception)
      .when(relationService)
      .saveRelation(relationDto, authentication, redirectAttributes);

    when(
      handleException.exceptionRelation(
        any(RuntimeException.class),
        eq(bindingResult)
      )
    ).thenReturn("redirect:/relation");

    String viewName = relationController.addRelation(
      relationDto,
      bindingResult,
      redirectAttributes,
      authentication
    );

    verify(handleException).exceptionRelation(eq(exception), eq(bindingResult));
    assertEquals("redirect:/relation", viewName);
  }
}
