package tn.enicarthage.gestionformation.participant.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.enums.Role;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.participant.dto.ParticipantDTO;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.mapper.ParticipantMapper;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.participant.service.ParticipantService;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import java.util.List;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ParticipantMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public ParticipantServiceImpl(ParticipantRepository participantRepository,
                                  UtilisateurRepository utilisateurRepository,
                                  ParticipantMapper mapper,
                                  PasswordEncoder passwordEncoder) {
        this.participantRepository = participantRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ParticipantDTO> listerTous() {
        return participantRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public ParticipantDTO trouverParId(Long id) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.participant.introuvable", id));
        return mapper.toDTO(participant);
    }

    @Override
    @Transactional
    public ParticipantDTO creer(ParticipantDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            // Anti-énumération : on ne révèle jamais qu'un email est déjà pris
            return dto;
        }

        Utilisateur compte = new Utilisateur();
        compte.setEmail(dto.getEmail());
        compte.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        compte.setRole(Role.PARTICIPANT);
        compte.setActif(false);

        Participant participant = new Participant();
        mapper.mettreAJourEntite(participant, dto);
        participant.setCompte(compte);

        return mapper.toDTO(participantRepository.save(participant));
    }

    @Override
    @Transactional
    public ParticipantDTO modifier(Long id, ParticipantDTO dto) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.participant.introuvable", id));
        mapper.mettreAJourEntite(participant, dto);
        return mapper.toDTO(participantRepository.save(participant));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException("erreur.participant.introuvable", id);
        }
        participantRepository.deleteById(id);
    }
}