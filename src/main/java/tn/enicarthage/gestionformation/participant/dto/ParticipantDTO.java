package tn.enicarthage.gestionformation.participant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantDTO {

    private Long id;

    @NotBlank(message = "{validation.participant.nomPrenom}")
    private String nomPrenom;

    @NotBlank(message = "{validation.participant.cin}")
    private String cin;

    @NotBlank(message = "{validation.participant.entreprise}")
    private String entreprise;

    private String telFix;
    private String fax;
    private String telPort;

    @Email(message = "{validation.email.invalide}")
    private String mail;

    // Champ transitoire, utilisé uniquement à l'inscription publique pour choisir le premier cycle
    private Long cycleId;

    @NotBlank
    @Email(message = "{validation.email.invalide}")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "{validation.motdepasse.complexite}"
    )
    private String motDePasse;
}