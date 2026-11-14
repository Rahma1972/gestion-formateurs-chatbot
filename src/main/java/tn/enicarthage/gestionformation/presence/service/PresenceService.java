package tn.enicarthage.gestionformation.presence.service;

import tn.enicarthage.gestionformation.cycle.dto.CycleGestionDTO;
import tn.enicarthage.gestionformation.cycle.dto.SeanceDTO;

import java.time.LocalDate;
import java.util.List;

public interface PresenceService {
    CycleGestionDTO chargerGestionCycle(Long cycleId);
    SeanceDTO chargerSeance(Long cycleId, LocalDate date);
    void enregistrerPresenceSeance(Long cycleId, LocalDate date, List<Long> participantsPresentsIds);
    void ajouterSupport(Long cycleId, String titre, String url);
    void supprimerSupport(Long supportId);
    long compterAbsencesNonJustifiees(Long participantId, Long cycleId);
}