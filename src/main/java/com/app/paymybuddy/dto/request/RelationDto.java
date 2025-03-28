package com.app.paymybuddy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RelationDto {

  @NotBlank(message = "l'Email est requis")
  @Email(message = "l'Email doit être valide")
  private String email;
}
