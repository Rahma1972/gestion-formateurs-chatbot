package tn.enicarthage.gestionformation.participant.service;

import tn.enicarthage.gestionformation.participant.dto.ParticipantDTO;

import java.util.List;

public interface ParticipantService {
    List<ParticipantDTO> listerTous();
    ParticipantDTO trouverParId(Long id);
    ParticipantDTO creer(ParticipantDTO dto);
    ParticipantDTO modifier(Long id, ParticipantDTO dto);
    void supprimer(Long id);
}