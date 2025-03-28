package com.app.paymybuddy.service;

import com.app.paymybuddy.dto.request.RelationDto;
import com.app.paymybuddy.exception.RelationAlreadyExistsException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.RelationRepository;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RelationService {

  private final RelationRepository relationRepository;
  private final UserRepository userRepository;

  /**
   * Save a new relation.
   * @param authentication
   * @param relationDto
   */
  @Transactional
  public void saveRelation(
    Authentication authentication,
    RelationDto relationDto
  ) {
    // 1. Get the email of the relation user
    //    And the current user id
    String relationUserEmail = relationDto.getEmail();
    Integer currentUserId =
      ((CustomUserDetails) authentication.getPrincipal()).getUserId();

    // 2. Check if the current user and the relation user are the same
    checkIfCurrentUserAndRelationUserAreTheSame(
      authentication.getName(),
      relationUserEmail
    );

    // 3. Find the relation user by email
    User relationUser = findRelationUserByEmail(relationUserEmail);

    // 4. Check if the relation already exists
    checkRelationExists(currentUserId, relationUser);

    // 5. Save the relation
    relationRepository.saveRelation(currentUserId, relationUser.getId());
  }

  /**
   * Check if the current user and the relation user are the same.
   * @param currentUserEmail
   * @param relationUserEmail
   */
  private void checkIfCurrentUserAndRelationUserAreTheSame(
    String currentUserEmail,
    String relationUserEmail
  ) {
    if (currentUserEmail.equals(relationUserEmail)) {
      throw new RelationAlreadyExistsException(
        "Vous ne pouvez pas vous ajouter en tant que relation"
      );
    }
  }

  /**
   * Find a relation user by email.
   * @param relationUserEmail
   * @return User
   */
  private User findRelationUserByEmail(String relationUserEmail) {
    return userRepository
      .findByEmailAndDeletedAtIsNull(relationUserEmail)
      .orElseThrow(() -> new UserNotFoundException("Relation non trouvée"));
  }

  /**
   * Check if a relation already exists between the current user and the relation user.
   * @param currentUserId
   * @param relationUser
   */
  private void checkRelationExists(Integer currentUserId, User relationUser) {
    if (
      relationRepository.existsRelationBetweenUserIdAndEmail(
        currentUserId,
        relationUser.getId()
      )
    ) {
      throw new RelationAlreadyExistsException(
        "Relation déjà existante avec cet utilisateur"
      );
    }
  }
}
