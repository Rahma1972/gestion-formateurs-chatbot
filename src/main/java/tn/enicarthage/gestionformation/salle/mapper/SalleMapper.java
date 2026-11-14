package tn.enicarthage.gestionformation.salle.mapper;

import org.springframework.stereotype.Component;
import tn.enicarthage.gestionformation.salle.dto.SalleDTO;
import tn.enicarthage.gestionformation.salle.entity.Salle;

@Component
public class SalleMapper {

    public SalleDTO toDTO(Salle salle) {
        SalleDTO dto = new SalleDTO();
        dto.setId(salle.getId());
        dto.setNom(salle.getNom());
        dto.setCapacite(salle.getCapacite());
        return dto;
    }

    public void mettreAJourEntite(Salle salle, SalleDTO dto) {
        salle.setNom(dto.getNom());
        salle.setCapacite(dto.getCapacite());
    }
}