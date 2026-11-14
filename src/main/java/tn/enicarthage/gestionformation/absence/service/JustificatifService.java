package tn.enicarthage.gestionformation.absence.service;

import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.gestionformation.absence.dto.JustificatifDTO;
import tn.enicarthage.gestionformation.absence.entity.JustificatifAbsence;

import java.util.List;

public interface JustificatifService {
    void deposer(Long presenceSeanceId, String description, MultipartFile fichier);
    List<JustificatifDTO> listerPourParticipant(Long participantId);
    List<JustificatifDTO> listerTout();
    void traiter(Long justificatifId, boolean accepter, String commentaire);
    JustificatifAbsence trouverParId(Long id);
    List<JustificatifDTO> listerAbsencesAJustifier(Long participantId);
}