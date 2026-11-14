package tn.enicarthage.gestionformation.utilisateur.service;

import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;

import java.util.List;

public interface UtilisateurService {
    List<Utilisateur> listerComptesEnAttente();
    void activerCompte(Long id);
    void changerMotDePasse(String email, String motDePasseActuel, String nouveauMotDePasse);
}