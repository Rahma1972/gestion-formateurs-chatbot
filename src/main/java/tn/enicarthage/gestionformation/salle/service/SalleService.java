package tn.enicarthage.gestionformation.salle.service;

import tn.enicarthage.gestionformation.salle.dto.SalleDTO;

import java.util.List;

public interface SalleService {
    List<SalleDTO> listerTous();
    SalleDTO trouverParId(Long id);
    SalleDTO creer(SalleDTO dto);
    SalleDTO modifier(Long id, SalleDTO dto);
    void supprimer(Long id);
}