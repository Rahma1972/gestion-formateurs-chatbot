package tn.enicarthage.gestionformation.formateur.mapper;

import org.springframework.stereotype.Component;
import tn.enicarthage.gestionformation.formateur.dto.FormateurDTO;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;

@Component
public class FormateurMapper {

    public FormateurDTO toDTO(Formateur formateur) {
        FormateurDTO dto = new FormateurDTO();
        dto.setId(formateur.getId());
        dto.setNomPrenom(formateur.getNomPrenom());
        dto.setSpecialite(formateur.getSpecialite());
        dto.setDirection(formateur.getDirection());
        dto.setEntreprise(formateur.getEntreprise());
        if (formateur.getCompte() != null) {
            dto.setEmail(formateur.getCompte().getEmail());
        }
        // motDePasse volontairement absent : on ne renvoie jamais le mot de passe vers la vue
        return dto;
    }

    public void mettreAJourEntite(Formateur formateur, FormateurDTO dto) {
        formateur.setNomPrenom(dto.getNomPrenom());
        formateur.setSpecialite(dto.getSpecialite());
        formateur.setDirection(dto.getDirection());
        formateur.setEntreprise(dto.getEntreprise());
    }
}