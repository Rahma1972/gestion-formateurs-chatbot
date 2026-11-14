package tn.enicarthage.gestionformation.inscription.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CycleDisponibleDTO {
    private Long id;
    private String theme;
    private LocalDate dateDeb;
    private LocalDate dateFin;
    private String salleNom;
    private int placesRestantes;
    private boolean dejaInscrit;
}