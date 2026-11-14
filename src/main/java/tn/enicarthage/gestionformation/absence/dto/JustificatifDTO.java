package tn.enicarthage.gestionformation.absence.dto;

import lombok.Getter;
import lombok.Setter;
import tn.enicarthage.gestionformation.common.enums.StatutJustificatif;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class JustificatifDTO {
    private Long id;
    private Long presenceSeanceId;
    private String participantNom;
    private String cycleTheme;
    private LocalDate dateAbsence;
    private String description;
    private String nomFichier;
    private boolean possedeFichier;
    private LocalDateTime dateDepot;
    private LocalDate dateLimite;
    private StatutJustificatif statut;
    private String commentaireAdmin;
    private boolean delaiDepasse;
}