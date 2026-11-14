package tn.enicarthage.gestionformation.participant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.gestionformation.common.enums.Role;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.participant.dto.ParticipantDTO;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.mapper.ParticipantMapper;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.participant.service.impl.ParticipantServiceImpl;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceImplTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ParticipantMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    private ParticipantDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ParticipantDTO();
        dto.setNomPrenom("Salma Trabelsi");
        dto.setCin("08234567");
        dto.setEntreprise("STEG");
        dto.setCycleId(1L);
        dto.setEmail("salma@steg.tn");
        dto.setMotDePasse("motdepasse123");
    }

    @Test
    void creer_devraitLeverExceptionSiEmailExisteDeja() {
        when(utilisateurRepository.existsByEmail("salma@steg.tn")).thenReturn(true);

        assertThrows(BusinessException.class, () -> participantService.creer(dto));
        verify(participantRepository, never()).save(any());
    }

    @Test
    void creer_devraitCreerCompteInactif() {
        when(utilisateurRepository.existsByEmail("salma@steg.tn")).thenReturn(false);
        when(passwordEncoder.encode("motdepasse123")).thenReturn("hash_simule");
        when(participantRepository.save(any(Participant.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(Participant.class))).thenReturn(dto);

        participantService.creer(dto);

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(captor.capture());

        Utilisateur compteCree = captor.getValue().getCompte();
        assertNotNull(compteCree);
        assertFalse(compteCree.isActif(), "Le compte doit être inactif à la création (en attente de validation admin)");
        assertEquals(Role.PARTICIPANT, compteCree.getRole());
    }

    @Test
    void trouverParId_devraitLeverExceptionSiIntrouvable() {
        when(participantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> participantService.trouverParId(99L));
    }

    @Test
    void supprimer_devraitReussirSiExiste() {
        when(participantRepository.existsById(1L)).thenReturn(true);

        participantService.supprimer(1L);

        verify(participantRepository, times(1)).deleteById(1L);
    }
}