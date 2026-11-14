package tn.enicarthage.gestionformation.formateur.service;

import tn.enicarthage.gestionformation.formateur.dto.FormateurDTO;

import java.util.List;

public interface FormateurService {
    List<FormateurDTO> listerTous();
    FormateurDTO trouverParId(Long id);
    FormateurDTO creer(FormateurDTO dto);
    FormateurDTO modifier(Long id, FormateurDTO dto);
    void supprimer(Long id);
}