package tn.enicarthage.gestionformation.utilisateur.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;
import tn.enicarthage.gestionformation.utilisateur.service.UtilisateurService;
import tn.enicarthage.gestionformation.audit.service.AuditService;
import tn.enicarthage.gestionformation.common.enums.TypeEvenement;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, AuditService auditService, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.auditService = auditService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Utilisateur> listerComptesEnAttente() {
        return utilisateurRepository.findAll().stream()
                .filter(u -> !u.isActif())
                .toList();
    }

    @Override
    @Transactional
    public void activerCompte(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.compte.introuvable" + id));
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
        auditService.enregistrer(TypeEvenement.COMPTE_ACTIVE, utilisateur.getEmail(), "Compte activé par l'administration");
    }
    @Override
    @Transactional
    public void changerMotDePasse(String email, String motDePasseActuel, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.compte.introuvable"));

        if (!passwordEncoder.matches(motDePasseActuel, utilisateur.getMotDePasse())) {
            throw new BusinessException("erreur.motdepasse.incorrect");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }
}