package com.app.paymybuddy.service;

import com.app.paymybuddy.dto.request.RelationDto;
import com.app.paymybuddy.dto.response.UserRelationDto;
import com.app.paymybuddy.exception.RelationAlreadyExistsException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.RelationRepository;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@AllArgsConstructor
public class RelationService {

  private final RelationRepository relationRepository;
  private final UserRepository userRepository;

  /**
   * Find the relations of the current user.
   * @param authentication
   * @return Set<UserRelationDto>
   */
  public Set<UserRelationDto> findUserRelations(Authentication authentication) {
    // 1. Get the current user id
    Integer currentUserId =
      ((CustomUserDetails) authentication.getPrincipal()).getUserId();

    // 2. Find the relations of the current user
    return relationRepository.findRelationsByUserId(currentUserId);
  }

  /**
   * Save a new relation.
   * @param authentication
   * @param relationDto
   */
  @Transactional
  public void saveRelation(
    RelationDto relationDto,
    Authentication authentication,
    RedirectAttributes redirectAttributes
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
    User relationUser = findUserByEmail(relationUserEmail);

    // 4. Check if the relation already exists
    checkRelationExists(currentUserId, relationUser);

    // 5. Save the relation
    relationRepository.saveRelation(currentUserId, relationUser.getId());

    // 6. Add a success message to the redirect attributes
    redirectAttributes.addFlashAttribute(
      "successMessage",
      "Relation ajoutée avec succès !!"
    );
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
  private User findUserByEmail(String relationUserEmail) {
    return userRepository
      .findByEmailAndDeletedAtIsNull(relationUserEmail)
      .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
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
