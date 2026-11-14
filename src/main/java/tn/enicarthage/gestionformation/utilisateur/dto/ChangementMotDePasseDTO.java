package tn.enicarthage.gestionformation.utilisateur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangementMotDePasseDTO {

    @NotBlank(message = "{validation.motdepasse.actuel}")
    private String motDePasseActuel;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "{validation.motdepasse.complexite}"
    )
    private String nouveauMotDePasse;

    @NotBlank(message = "{validation.motdepasse.confirmation.obligatoire}")
    private String confirmationMotDePasse;
}