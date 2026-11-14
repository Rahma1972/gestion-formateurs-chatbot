package tn.enicarthage.gestionformation.cycle.dto;

import lombok.Getter;
import lombok.Setter;
import tn.enicarthage.gestionformation.support.dto.SupportCoursDTO;

import java.util.List;

@Getter
@Setter
public class CycleGestionDTO {
    private Long id;
    private String theme;
    private String dateDeb;
    private String dateFin;
    private List<java.time.LocalDate> joursCycle;
    private List<SupportCoursDTO> supports;
}