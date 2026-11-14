package tn.enicarthage.gestionformation.inscription.dto;

import lombok.Getter;
import lombok.Setter;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InscriptionDTO {
    private Long id;
    private Long cycleId;
    private String cycleTheme;
    private LocalDate dateDeb;
    private LocalDate dateFin;
    private LocalDateTime dateInscription;
    private StatutInscription statut;
    private boolean cycleTermine;
    private boolean eligibleAttestation;
    private boolean dejaEvalue;
}