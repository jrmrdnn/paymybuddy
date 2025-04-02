package com.app.paymybuddy.repository;

import com.app.paymybuddy.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  /**
   * Find a user by id
   * @param UserId
   * @return Optional User
   */
  @Query("SELECT u FROM User u WHERE u.id = :userId")
  Optional<User> findById(@Param("userId") int userId);

  /**
   * Find a user by email
   * @param email
   * @return Optional User
   */
  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  /**
   * Find a user by id and deletedAt is null
   * @param UserId
   * @return Optional User
   */
  @Query("SELECT u FROM User u WHERE u.id = :userId AND u.deletedAt IS NULL")
  Optional<User> findByIdAndDeletedAtIsNull(@Param("userId") Integer userId);

  /**
   * Find a user by email and deletedAt is null
   * @param email
   * @return Optional User
   */
  @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
  Optional<User> findByEmailAndDeletedAtIsNull(@Param("email") String email);
}
