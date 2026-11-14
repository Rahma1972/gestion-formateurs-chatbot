package tn.enicarthage.gestionformation.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DashboardDTO {
    private long totalFormateurs;
    private long totalParticipants;
    private long totalCycles;
    private double tauxRemplissageMoyen;
    private long comptesEnAttente;
    private long justificatifsEnAttente;

    private Map<String, Long> cyclesParStatut;
    private List<String> moisLabels;
    private List<Long> cyclesParMois;
    private Map<String, Double> moyenneEvaluationParCycle;
}