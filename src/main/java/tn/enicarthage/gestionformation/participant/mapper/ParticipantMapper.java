package tn.enicarthage.gestionformation.participant.mapper;

import org.springframework.stereotype.Component;
import tn.enicarthage.gestionformation.participant.dto.ParticipantDTO;
import tn.enicarthage.gestionformation.participant.entity.Participant;

@Component
public class ParticipantMapper {

    public ParticipantDTO toDTO(Participant participant) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(participant.getId());
        dto.setNomPrenom(participant.getNomPrenom());
        dto.setCin(participant.getCin());
        dto.setEntreprise(participant.getEntreprise());
        dto.setTelFix(participant.getTelFix());
        dto.setFax(participant.getFax());
        dto.setTelPort(participant.getTelPort());
        dto.setMail(participant.getMail());

        if (participant.getCompte() != null) {
            dto.setEmail(participant.getCompte().getEmail());
        }

        return dto;
    }

    public void mettreAJourEntite(Participant participant, ParticipantDTO dto) {
        participant.setNomPrenom(dto.getNomPrenom());
        participant.setCin(dto.getCin());
        participant.setEntreprise(dto.getEntreprise());
        participant.setTelFix(dto.getTelFix());
        participant.setFax(dto.getFax());
        participant.setTelPort(dto.getTelPort());
        participant.setMail(dto.getMail());
        // Le cycle n'est plus géré ici : voir InscriptionService
    }
}