package com.app.paymybuddy.dto.request;

import com.app.paymybuddy.constants.RegexpConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransferDto {

  @NotBlank(message = "Le destinataire ne peut pas être vide")
  private String email;

  @NotBlank(message = "La description ne peut pas être vide")
  @Size(
    min = 3,
    max = 50,
    message = "La description doit contenir entre 3 et 50 caractères"
  )
  @Pattern(
    regexp = RegexpConstants.DESCRIPTION_REGEXP,
    message = "La description ne peut contenir que des lettres, des chiffres, des espaces, des tirets, des apostrophes et points"
  )
  private String description;

  @NotBlank(message = "Le montant ne peut pas être vide")
  @Pattern(
    regexp = RegexpConstants.AMOUNT_REGEXP,
    message = "Le montant doit être un nombre décimal positif"
  )
  private String amount;
}
