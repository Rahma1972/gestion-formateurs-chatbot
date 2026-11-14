package tn.enicarthage.gestionformation.dashboard.service.impl;

import org.springframework.stereotype.Service;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.dashboard.dto.DashboardDTO;
import tn.enicarthage.gestionformation.dashboard.service.DashboardService;
import tn.enicarthage.gestionformation.evaluation.service.EvaluationService;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;
import tn.enicarthage.gestionformation.absence.repository.JustificatifAbsenceRepository;
import tn.enicarthage.gestionformation.common.enums.StatutJustificatif;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final FormateurRepository formateurRepository;
    private final ParticipantRepository participantRepository;
    private final CycleRepository cycleRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JustificatifAbsenceRepository justificatifAbsenceRepository;
    private final EvaluationService evaluationService;

    public DashboardServiceImpl(FormateurRepository formateurRepository, ParticipantRepository participantRepository,
                                CycleRepository cycleRepository, InscriptionRepository inscriptionRepository,
                                UtilisateurRepository utilisateurRepository,
                                JustificatifAbsenceRepository justificatifAbsenceRepository,
                                EvaluationService evaluationService) {
        this.formateurRepository = formateurRepository;
        this.participantRepository = participantRepository;
        this.cycleRepository = cycleRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.justificatifAbsenceRepository = justificatifAbsenceRepository;
        this.evaluationService = evaluationService;
    }

    @Override
    public DashboardDTO construireDashboard() {
        DashboardDTO dto = new DashboardDTO();

        List<Cycle> cycles = cycleRepository.findAll();

        dto.setTotalFormateurs(formateurRepository.count());
        dto.setTotalParticipants(participantRepository.count());
        dto.setTotalCycles(cycles.size());

        dto.setComptesEnAttente(utilisateurRepository.findAll().stream().filter(u -> !u.isActif()).count());
        dto.setJustificatifsEnAttente(justificatifAbsenceRepository.findAll().stream()
                .filter(j -> j.getStatut() == StatutJustificatif.EN_ATTENTE).count());

        double tauxTotal = 0;
        int nbCyclesAvecSalle = 0;
        for (Cycle cycle : cycles) {
            if (cycle.getSalle() != null && cycle.getSalle().getCapacite() != null && cycle.getSalle().getCapacite() > 0) {
                long inscrits = inscriptionRepository.countByCycle_IdAndStatut(cycle.getId(), StatutInscription.ACTIVE);
                tauxTotal += (double) inscrits / cycle.getSalle().getCapacite();
                nbCyclesAvecSalle++;
            }
        }
        dto.setTauxRemplissageMoyen(nbCyclesAvecSalle > 0 ? (tauxTotal / nbCyclesAvecSalle) * 100 : 0);

        Map<String, Long> parStatut = cycles.stream()
                .collect(Collectors.groupingBy(c -> c.getStatut().name(), Collectors.counting()));
        dto.setCyclesParStatut(parStatut);

        Map<String, Long> parMois = new LinkedHashMap<>();
        cycles.stream()
                .sorted(Comparator.comparing(Cycle::getDateDeb))
                .forEach(c -> {
                    String cle = c.getDateDeb().getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + c.getDateDeb().getYear();
                    parMois.merge(cle, 1L, Long::sum);
                });
        dto.setMoisLabels(new ArrayList<>(parMois.keySet()));
        dto.setCyclesParMois(new ArrayList<>(parMois.values()));

        Map<String, Double> moyennes = new LinkedHashMap<>();
        for (Cycle cycle : cycles) {
            Double moyenne = evaluationService.moyenneCycle(cycle.getId());
            if (moyenne != null) {
                moyennes.put(cycle.getTheme(), Math.round(moyenne * 10) / 10.0);
            }
        }
        dto.setMoyenneEvaluationParCycle(moyennes);

        return dto;
    }
}