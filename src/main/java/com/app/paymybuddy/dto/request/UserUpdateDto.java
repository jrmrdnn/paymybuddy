package com.app.paymybuddy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

  @NotBlank(message = "Le nom d'utilisateur est requis")
  @Size(
    min = 2,
    max = 50,
    message = "Le nom d'utilisateur doit avoir entre 2 et 50 caractères"
  )
  private String username;

  @NotBlank(message = "l'Email est requis")
  @Email(message = "l'Email doit être valide")
  private String email;

  private String password;
}
