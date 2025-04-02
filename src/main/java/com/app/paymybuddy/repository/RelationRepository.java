package com.app.paymybuddy.repository;

import com.app.paymybuddy.dto.response.UserRelationDto;
import com.app.paymybuddy.model.Relation;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {
  /**
   * Find all relations of a user
   * @param userId
   * @return Set<UserRelationDto>
   */
  @Query(
    "SELECT new com.app.paymybuddy.dto.response.UserRelationDto(r.relationUser.username, r.relationUser.email) " +
    "FROM Relation r WHERE r.user.id = :userId"
  )
  Set<UserRelationDto> findRelationsByUserId(@Param("userId") Integer userId);

  /**
   * Check if a relation exists between two users
   * @param userId
   * @param relationUserEmail
   * @return boolean
   */
  @Query(
    "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
    "FROM Relation r WHERE r.user.id = :userId " +
    "AND r.relationUser.id = :relationUserId"
  )
  boolean existsRelationBetweenUserIdAndEmail(
    @Param("userId") Integer userId,
    @Param("relationUserId") Integer relationUserId
  );

  /**
   * Save a relation
   * @param userId
   * @param relationUserId
   * @return void
   */
  @Modifying
  @Query(
    "INSERT INTO Relation r (r.user.id, r.relationUser.id) " +
    "VALUES (:userId, :relationUserId)"
  )
  void saveRelation(
    @Param("userId") Integer userId,
    @Param("relationUserId") Integer relationUserId
  );
}
