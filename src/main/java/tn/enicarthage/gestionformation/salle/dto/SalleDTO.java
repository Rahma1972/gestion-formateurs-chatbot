package tn.enicarthage.gestionformation.salle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalleDTO {

    private Long id;

    @NotBlank(message = "{validation.salle.nom}")
    private String nom;

    @Positive(message = "{validation.salle.capacite}")
    private Integer capacite;
}