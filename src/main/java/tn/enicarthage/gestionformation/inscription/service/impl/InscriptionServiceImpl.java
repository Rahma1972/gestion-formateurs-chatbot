package tn.enicarthage.gestionformation.inscription.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.inscription.dto.CycleDisponibleDTO;
import tn.enicarthage.gestionformation.inscription.dto.InscriptionDTO;
import tn.enicarthage.gestionformation.inscription.entity.Inscription;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.inscription.service.InscriptionService;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.attestation.service.AttestationService;
import tn.enicarthage.gestionformation.evaluation.service.EvaluationService;

import java.time.LocalDate;
import java.util.List;

@Service
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final CycleRepository cycleRepository;
    private final ParticipantRepository participantRepository;
    private final AttestationService attestationService;
    private final EvaluationService evaluationService;

    public InscriptionServiceImpl(InscriptionRepository inscriptionRepository, CycleRepository cycleRepository,
                                  ParticipantRepository participantRepository, AttestationService attestationService,
                                  EvaluationService evaluationService) {
        this.inscriptionRepository = inscriptionRepository;
        this.cycleRepository = cycleRepository;
        this.participantRepository = participantRepository;
        this.attestationService = attestationService;
        this.evaluationService = evaluationService;
    }

    @Override
    public List<CycleDisponibleDTO> listerCyclesDisponibles(Long participantId) {
        List<Cycle> tousLesCycles = cycleRepository.findAll();
        List<Inscription> inscriptionsActives = inscriptionRepository.findByParticipant_IdAndStatut(participantId, StatutInscription.ACTIVE);

        return tousLesCycles.stream().map(cycle -> {
            CycleDisponibleDTO dto = new CycleDisponibleDTO();
            dto.setId(cycle.getId());
            dto.setTheme(cycle.getTheme());
            dto.setDateDeb(cycle.getDateDeb());
            dto.setDateFin(cycle.getDateFin());
            dto.setSalleNom(cycle.getSalle() != null ? cycle.getSalle().getNom() : "");

            long inscrits = inscriptionRepository.countByCycle_IdAndStatut(cycle.getId(), StatutInscription.ACTIVE);
            int placesMax = (cycle.getSalle() != null && cycle.getSalle().getCapacite() != null) ? cycle.getSalle().getCapacite() : 0;
            dto.setPlacesRestantes(Math.max(0, placesMax - (int) inscrits));

            boolean dejaInscrit = inscriptionsActives.stream().anyMatch(i -> i.getCycle().getId().equals(cycle.getId()));
            dto.setDejaInscrit(dejaInscrit);

            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void sInscrire(Long participantId, Long cycleId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.participant.introuvable", participantId));
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        boolean dejaInscrit = inscriptionRepository.findByParticipant_IdAndCycle_IdAndStatut(participantId, cycleId, StatutInscription.ACTIVE).isPresent();
        if (dejaInscrit) {
            throw new BusinessException("erreur.inscription.dejaInscrit");
        }

        long inscrits = inscriptionRepository.countByCycle_IdAndStatut(cycleId, StatutInscription.ACTIVE);
        int placesMax = (cycle.getSalle() != null && cycle.getSalle().getCapacite() != null) ? cycle.getSalle().getCapacite() : 0;
        if (inscrits >= placesMax) {
            throw new BusinessException("erreur.inscription.completLe");
        }

        List<Inscription> inscriptionsActives = inscriptionRepository.findByParticipant_IdAndStatut(participantId, StatutInscription.ACTIVE);
        boolean chevauchement = inscriptionsActives.stream().anyMatch(i -> {
            Cycle autreCycle = i.getCycle();
            return !cycle.getDateFin().isBefore(autreCycle.getDateDeb()) && !cycle.getDateDeb().isAfter(autreCycle.getDateFin());
        });
        if (chevauchement) {
            throw new BusinessException("erreur.inscription.chevauchement");
        }

        Inscription inscription = new Inscription();
        inscription.setParticipant(participant);
        inscription.setCycle(cycle);
        inscription.setStatut(StatutInscription.ACTIVE);
        inscriptionRepository.save(inscription);
    }

    @Override
    @Transactional
    public void annuler(Long participantId, Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.inscription.introuvable", inscriptionId));

        if (!inscription.getParticipant().getId().equals(participantId)) {
            throw new BusinessException("erreur.acces.inscription");
        }

        inscription.setStatut(StatutInscription.ANNULEE);
        inscriptionRepository.save(inscription);
    }

    @Override
    public List<InscriptionDTO> listerHistorique(Long participantId) {
        return inscriptionRepository.findByParticipant_Id(participantId).stream().map(i -> {
            InscriptionDTO dto = new InscriptionDTO();
            dto.setId(i.getId());
            dto.setCycleId(i.getCycle().getId());
            dto.setCycleTheme(i.getCycle().getTheme());
            dto.setDateDeb(i.getCycle().getDateDeb());
            dto.setDateFin(i.getCycle().getDateFin());
            dto.setDateInscription(i.getDateInscription());
            dto.setStatut(i.getStatut());


            boolean termine = !i.getCycle().getDateFin().isAfter(LocalDate.now());
            dto.setCycleTermine(termine);

            if (termine && i.getStatut() == StatutInscription.ACTIVE) {
                dto.setEligibleAttestation(attestationService.estEligible(participantId, i.getCycle().getId()));
            } else {
                dto.setEligibleAttestation(false);
            }

            dto.setDejaEvalue(evaluationService.dejaEvalue(participantId, i.getCycle().getId()));
            return dto;
        }).toList();
    }

    @Override
    public Long trouverCyclePrincipal(Long participantId) {
        return inscriptionRepository.findByParticipant_IdAndStatut(participantId, StatutInscription.ACTIVE)
                .stream().findFirst().map(i -> i.getCycle().getId()).orElse(null);
    }
}