package tn.enicarthage.gestionformation.inscription.service;

import tn.enicarthage.gestionformation.inscription.dto.CycleDisponibleDTO;
import tn.enicarthage.gestionformation.inscription.dto.InscriptionDTO;

import java.util.List;

public interface InscriptionService {
    List<CycleDisponibleDTO> listerCyclesDisponibles(Long participantId);
    void sInscrire(Long participantId, Long cycleId);
    void annuler(Long participantId, Long inscriptionId);
    List<InscriptionDTO> listerHistorique(Long participantId);
    Long trouverCyclePrincipal(Long participantId);
}