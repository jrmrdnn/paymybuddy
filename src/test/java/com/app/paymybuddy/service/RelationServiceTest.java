package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.RelationDto;
import com.app.paymybuddy.exception.RelationAlreadyExistsException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.RelationRepository;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class RelationServiceTest {

  @Mock
  private RelationRepository relationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private RelationService relationService;

  private static final String CURRENT_EMAIL = "current@example.com";
  private static final String RELATION_EMAIL = "relation@example.com";
  private RelationDto relationDto;

  @BeforeEach
  void setUp() {
    relationDto = new RelationDto();
    relationDto.setEmail(RELATION_EMAIL);
  }

  @Test
  void findUserRelations_shouldReturnUserRelations() {
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);

    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(customUserDetails.getUserId()).thenReturn(1);

    relationService.findUserRelations(authentication);

    verify(relationRepository, times(1)).findRelationsByUserId(1);
  }

  @Test
  void saveRelation_shouldThrowExceptionWhenCurrentUserAndRelationUserAreTheSame() {
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(authentication.getName()).thenReturn(RELATION_EMAIL);

    assertThrows(RelationAlreadyExistsException.class, () ->
      relationService.saveRelation(authentication, relationDto)
    );
  }

  @Test
  void saveRelation_shouldThrowExceptionWhenRelationUserNotFound() {
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(authentication.getName()).thenReturn(CURRENT_EMAIL);

    when(
      userRepository.findByEmailAndDeletedAtIsNull(RELATION_EMAIL)
    ).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () ->
      relationService.saveRelation(authentication, relationDto)
    );
  }

  @Test
  void saveRelation_shouldThrowExceptionWhenRelationAlreadyExists() {
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(authentication.getName()).thenReturn(CURRENT_EMAIL);

    when(customUserDetails.getUserId()).thenReturn(1);

    User relationUser = new User();
    relationUser.setId(2);

    when(
      userRepository.findByEmailAndDeletedAtIsNull(RELATION_EMAIL)
    ).thenReturn(Optional.of(relationUser));
    when(
      relationRepository.existsRelationBetweenUserIdAndEmail(1, 2)
    ).thenReturn(true);

    assertThrows(RelationAlreadyExistsException.class, () ->
      relationService.saveRelation(authentication, relationDto)
    );
  }

  @Test
  void saveRelation_shouldSaveRelationSuccessfully() {
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(customUserDetails.getUserId()).thenReturn(1);
    when(authentication.getName()).thenReturn(CURRENT_EMAIL);

    User relationUser = new User();
    relationUser.setId(2);

    when(
      userRepository.findByEmailAndDeletedAtIsNull(RELATION_EMAIL)
    ).thenReturn(Optional.of(relationUser));
    when(
      relationRepository.existsRelationBetweenUserIdAndEmail(1, 2)
    ).thenReturn(false);

    relationService.saveRelation(authentication, relationDto);

    verify(relationRepository, times(1)).saveRelation(1, 2);
  }
}
