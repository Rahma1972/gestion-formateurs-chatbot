package tn.enicarthage.gestionformation.cycle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CycleDTO {

    private Long id;

    private String numAct;


    @NotBlank(message = "{validation.cycle.theme}")
    private String theme;

    @NotNull(message = "{validation.cycle.dateDeb}")
    private LocalDate dateDeb;

    @NotNull(message = "{validation.cycle.dateFin}")
    private LocalDate dateFin;

    private String statut;

    @NotNull(message = "{validation.cycle.salle}")
    private Long salleId;

    private String salleNom;

    @NotNull(message = "{validation.cycle.formateurs}")
    private List<Long> formateurIds;

    private List<String> formateurNoms;


}