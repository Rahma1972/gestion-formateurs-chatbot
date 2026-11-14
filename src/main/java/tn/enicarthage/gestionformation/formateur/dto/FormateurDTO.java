package tn.enicarthage.gestionformation.formateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormateurDTO {

    private Long id;

    @NotBlank(message = "{validation.formateur.nomPrenom}")
    private String nomPrenom;

    @NotBlank(message = "{validation.formateur.specialite}")
    private String specialite;

    @NotBlank(message = "{validation.champ.obligatoire}")
    private String direction;

    @NotBlank(message = "{validation.champ.obligatoire}")
    private String entreprise;

    @NotBlank
    @Email(message = "{validation.email.invalide}")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "{validation.motdepasse.complexite}"
    )
    private String motDePasse;
}