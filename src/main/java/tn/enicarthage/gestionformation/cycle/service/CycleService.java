package tn.enicarthage.gestionformation.cycle.service;

import tn.enicarthage.gestionformation.cycle.dto.CycleDTO;

import java.util.List;

public interface CycleService {
    List<CycleDTO> listerTous();
    CycleDTO trouverParId(Long id);
    CycleDTO creer(CycleDTO dto);
    CycleDTO modifier(Long id, CycleDTO dto);
    void supprimer(Long id);
}