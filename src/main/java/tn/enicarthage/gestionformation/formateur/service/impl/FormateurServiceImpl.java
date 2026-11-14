package tn.enicarthage.gestionformation.formateur.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.enums.Role;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.formateur.dto.FormateurDTO;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.formateur.mapper.FormateurMapper;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.formateur.service.FormateurService;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import java.util.List;

@Service
public class FormateurServiceImpl implements FormateurService {

    private final FormateurRepository formateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FormateurMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public FormateurServiceImpl(FormateurRepository formateurRepository,
                                UtilisateurRepository utilisateurRepository,
                                FormateurMapper mapper,
                                PasswordEncoder passwordEncoder) {
        this.formateurRepository = formateurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<FormateurDTO> listerTous() {
        return formateurRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public FormateurDTO trouverParId(Long id) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.formateur.introuvable", id));
        return mapper.toDTO(formateur);
    }

    @Override
    @Transactional
    public FormateurDTO creer(FormateurDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("erreur.email.dejaUtilise");
        }

        Utilisateur compte = new Utilisateur();
        compte.setEmail(dto.getEmail());
        compte.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        compte.setRole(Role.FORMATEUR);

        Formateur formateur = new Formateur();
        mapper.mettreAJourEntite(formateur, dto);
        formateur.setCompte(compte);

        return mapper.toDTO(formateurRepository.save(formateur));
    }

    @Override
    @Transactional
    public FormateurDTO modifier(Long id, FormateurDTO dto) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.formateur.introuvable", id));
        mapper.mettreAJourEntite(formateur, dto);
        return mapper.toDTO(formateurRepository.save(formateur));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!formateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("erreur.formateur.introuvable", id);
        }
        formateurRepository.deleteById(id);
    }
}