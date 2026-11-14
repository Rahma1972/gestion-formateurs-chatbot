package tn.enicarthage.gestionformation.absence.service.impl;
import tn.enicarthage.gestionformation.absence.dto.JustificatifDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.gestionformation.absence.entity.JustificatifAbsence;
import tn.enicarthage.gestionformation.absence.repository.JustificatifAbsenceRepository;
import tn.enicarthage.gestionformation.absence.service.FileStorageService;
import tn.enicarthage.gestionformation.absence.service.JustificatifService;
import tn.enicarthage.gestionformation.common.enums.StatutJustificatif;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.presence.entity.PresenceSeance;
import tn.enicarthage.gestionformation.presence.repository.PresenceSeanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JustificatifServiceImpl implements JustificatifService {

    private static final int DELAI_JUSTIFICATIF_JOURS = 3;

    private final JustificatifAbsenceRepository justificatifRepository;
    private final PresenceSeanceRepository presenceSeanceRepository;
    private final FileStorageService fileStorageService;

    public JustificatifServiceImpl(JustificatifAbsenceRepository justificatifRepository,
                                   PresenceSeanceRepository presenceSeanceRepository,
                                   FileStorageService fileStorageService) {
        this.justificatifRepository = justificatifRepository;
        this.presenceSeanceRepository = presenceSeanceRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public void deposer(Long presenceSeanceId, String description, MultipartFile fichier) {
        PresenceSeance seance = presenceSeanceRepository.findById(presenceSeanceId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.absence.aucune"));

        if (seance.isPresent()) {
            throw new BusinessException("erreur.absence.aucune");
        }
        if (justificatifRepository.findByPresenceSeance_Id(presenceSeanceId).isPresent()) {
            throw new BusinessException("erreur.justificatif.dejaDepose");
        }

        LocalDate dateLimite = seance.getDateSeance().plusDays(DELAI_JUSTIFICATIF_JOURS);
        if (LocalDate.now().isAfter(dateLimite)) {
            throw new BusinessException("erreur.justificatif.delaiDepasse");
        }

        if ((description == null || description.isBlank()) && (fichier == null || fichier.isEmpty())) {
            throw new BusinessException("erreur.justificatif.vide");
        }

        JustificatifAbsence justificatif = new JustificatifAbsence();
        justificatif.setPresenceSeance(seance);
        justificatif.setDescription(description);
        justificatif.setDateLimite(dateLimite);

        if (fichier != null && !fichier.isEmpty()) {
            String nomStocke = fileStorageService.stocker(fichier);
            justificatif.setCheminFichier(nomStocke);
            justificatif.setNomFichier(fichier.getOriginalFilename());
        }

        justificatifRepository.save(justificatif);
    }

    @Override
    public List<JustificatifDTO> listerPourParticipant(Long participantId) {
        return justificatifRepository.findAllByOrderByDateDepotDesc().stream()
                .filter(j -> j.getPresenceSeance().getParticipant().getId().equals(participantId))
                .map(this::versDTO)
                .toList();
    }

    @Override
    public List<JustificatifDTO> listerTout() {
        return justificatifRepository.findAllByOrderByDateDepotDesc().stream().map(this::versDTO).toList();
    }

    @Override
    @Transactional
    public void traiter(Long justificatifId, boolean accepter, String commentaire) {
        JustificatifAbsence justificatif = justificatifRepository.findById(justificatifId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.justificatif.introuvable", justificatifId));

        if (justificatif.getStatut() != StatutJustificatif.EN_ATTENTE) {
            throw new BusinessException("erreur.justificatif.dejaTraite");
        }

        justificatif.setStatut(accepter ? StatutJustificatif.ACCEPTE : StatutJustificatif.REFUSE);
        justificatif.setCommentaireAdmin(commentaire);
        justificatif.setDateTraitement(LocalDateTime.now());
        justificatifRepository.save(justificatif);

        if (accepter) {
            PresenceSeance seance = justificatif.getPresenceSeance();
            seance.setJustifie(true);
            presenceSeanceRepository.save(seance);
        }
    }

    @Override
    public JustificatifAbsence trouverParId(Long id) {
        return justificatifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.justificatif.introuvable", id));
    }

    @Override
    public List<JustificatifDTO> listerAbsencesAJustifier(Long participantId) {
        return presenceSeanceRepository.findByParticipant_Id(participantId).stream()
                .filter(s -> !s.isPresent() && !s.isJustifie())
                .filter(s -> justificatifRepository.findByPresenceSeance_Id(s.getId()).isEmpty())
                .map(s -> {
                    JustificatifDTO dto = new JustificatifDTO();
                    dto.setPresenceSeanceId(s.getId());
                    dto.setDateAbsence(s.getDateSeance());
                    LocalDate limite = s.getDateSeance().plusDays(DELAI_JUSTIFICATIF_JOURS);
                    dto.setDateLimite(limite);
                    dto.setDelaiDepasse(LocalDate.now().isAfter(limite));
                    return dto;
                }).toList();
    }

    private JustificatifDTO versDTO(JustificatifAbsence j) {
        JustificatifDTO dto = new JustificatifDTO();
        dto.setId(j.getId());
        dto.setPresenceSeanceId(j.getPresenceSeance().getId());
        dto.setParticipantNom(j.getPresenceSeance().getParticipant().getNomPrenom());
        dto.setCycleTheme(j.getPresenceSeance().getCycle().getTheme());
        dto.setDateAbsence(j.getPresenceSeance().getDateSeance());
        dto.setDescription(j.getDescription());
        dto.setNomFichier(j.getNomFichier());
        dto.setPossedeFichier(j.getCheminFichier() != null);
        dto.setDateDepot(j.getDateDepot());
        dto.setDateLimite(j.getDateLimite());
        dto.setStatut(j.getStatut());
        dto.setCommentaireAdmin(j.getCommentaireAdmin());
        return dto;
    }
}