package com.app.paymybuddy.dto.request;

import com.app.paymybuddy.constants.RegexpConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {

  @NotBlank(message = "Le nom d'utilisateur est requis.")
  @Size(
    min = 2,
    max = 50,
    message = "Le nom d'utilisateur doit avoir entre 2 et 50 caractères."
  )
  private String username;

  @NotBlank(message = "Email est requis.")
  @Email(message = "Email doit être valide.")
  private String email;

  @NotBlank(message = "Le mot de passe est requis.")
  @Size(
    min = 8,
    max = 50,
    message = "Le mot de passe doit être entre 8 et 50 caractères."
  )
  @Pattern(
    regexp = RegexpConstants.PASSWORD_REGEXP,
    message = "Le mot de passe doit contenir au moins une lettre minuscule, une lettre majuscule, un chiffre et un caractère spécial."
  )
  private String password;
}
