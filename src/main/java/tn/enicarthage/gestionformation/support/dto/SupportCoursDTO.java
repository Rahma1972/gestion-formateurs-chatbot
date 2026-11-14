package tn.enicarthage.gestionformation.support.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportCoursDTO {
    private Long id;

    @NotBlank(message = "{validation.support.titre}")
    private String titre;

    @NotBlank(message = "{validation.support.url}")
    private String url;
}