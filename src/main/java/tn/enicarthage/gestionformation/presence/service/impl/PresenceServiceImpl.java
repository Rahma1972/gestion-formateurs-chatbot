package tn.enicarthage.gestionformation.presence.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.dto.CycleGestionDTO;
import tn.enicarthage.gestionformation.cycle.dto.PresenceParticipantDTO;
import tn.enicarthage.gestionformation.cycle.dto.SeanceDTO;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.inscription.entity.Inscription;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.presence.entity.PresenceSeance;
import tn.enicarthage.gestionformation.presence.repository.PresenceSeanceRepository;
import tn.enicarthage.gestionformation.presence.service.PresenceService;
import tn.enicarthage.gestionformation.support.dto.SupportCoursDTO;
import tn.enicarthage.gestionformation.support.entity.SupportCours;
import tn.enicarthage.gestionformation.support.repository.SupportCoursRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PresenceServiceImpl implements PresenceService {

    private final CycleRepository cycleRepository;
    private final InscriptionRepository inscriptionRepository;
    private final PresenceSeanceRepository presenceSeanceRepository;
    private final SupportCoursRepository supportCoursRepository;

    public PresenceServiceImpl(CycleRepository cycleRepository, InscriptionRepository inscriptionRepository,
                               PresenceSeanceRepository presenceSeanceRepository, SupportCoursRepository supportCoursRepository) {
        this.cycleRepository = cycleRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.presenceSeanceRepository = presenceSeanceRepository;
        this.supportCoursRepository = supportCoursRepository;
    }

    private List<Participant> participantsDuCycle(Long cycleId) {
        return inscriptionRepository.findByCycle_IdAndStatut(cycleId, StatutInscription.ACTIVE).stream()
                .map(Inscription::getParticipant)
                .toList();
    }

    @Override
    public CycleGestionDTO chargerGestionCycle(Long cycleId) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        List<LocalDate> jours = new ArrayList<>();
        LocalDate courant = cycle.getDateDeb();
        while (!courant.isAfter(cycle.getDateFin())) {
            jours.add(courant);
            courant = courant.plusDays(1);
        }

        List<SupportCoursDTO> supports = supportCoursRepository.findByCycle_Id(cycleId).stream().map(s -> {
            SupportCoursDTO dto = new SupportCoursDTO();
            dto.setId(s.getId());
            dto.setTitre(s.getTitre());
            dto.setUrl(s.getUrl());
            return dto;
        }).toList();

        CycleGestionDTO dto = new CycleGestionDTO();
        dto.setId(cycle.getId());
        dto.setTheme(cycle.getTheme());
        dto.setDateDeb(cycle.getDateDeb().toString());
        dto.setDateFin(cycle.getDateFin().toString());
        dto.setJoursCycle(jours);
        dto.setSupports(supports);
        return dto;
    }

    @Override
    public SeanceDTO chargerSeance(Long cycleId, LocalDate date) {
        List<Participant> participants = participantsDuCycle(cycleId);

        List<PresenceParticipantDTO> presences = participants.stream().map(p -> {
            PresenceParticipantDTO dto = new PresenceParticipantDTO();
            dto.setParticipantId(p.getId());
            dto.setParticipantNom(p.getNomPrenom());
            boolean present = presenceSeanceRepository.findByParticipant_IdAndCycle_IdAndDateSeance(p.getId(), cycleId, date)
                    .map(PresenceSeance::isPresent)
                    .orElse(true);
            dto.setPresent(present);
            dto.setTotalAbsencesCycle(compterAbsencesNonJustifiees(p.getId(), cycleId));
            return dto;
        }).toList();

        SeanceDTO dto = new SeanceDTO();
        dto.setDate(date);
        dto.setPresences(presences);
        return dto;
    }

    @Override
    @Transactional
    public void enregistrerPresenceSeance(Long cycleId, LocalDate date, List<Long> participantsPresentsIds) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        List<Participant> participants = participantsDuCycle(cycleId);

        for (Participant participant : participants) {
            PresenceSeance seance = presenceSeanceRepository
                    .findByParticipant_IdAndCycle_IdAndDateSeance(participant.getId(), cycleId, date)
                    .orElseGet(() -> {
                        PresenceSeance nouvelle = new PresenceSeance();
                        nouvelle.setParticipant(participant);
                        nouvelle.setCycle(cycle);
                        nouvelle.setDateSeance(date);
                        return nouvelle;
                    });
            seance.setPresent(participantsPresentsIds.contains(participant.getId()));
            presenceSeanceRepository.save(seance);
        }
    }

    @Override
    public long compterAbsencesNonJustifiees(Long participantId, Long cycleId) {
        return presenceSeanceRepository.countByParticipant_IdAndCycle_IdAndPresentFalseAndJustifieFalse(participantId, cycleId);
    }

    @Override
    @Transactional
    public void ajouterSupport(Long cycleId, String titre, String url) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        SupportCours support = new SupportCours();
        support.setTitre(titre);
        support.setUrl(url);
        support.setCycle(cycle);
        supportCoursRepository.save(support);
    }

    @Override
    @Transactional
    public void supprimerSupport(Long supportId) {
        supportCoursRepository.deleteById(supportId);
    }
}