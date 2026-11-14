package tn.enicarthage.gestionformation.formateur.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.formateur.dto.FormateurDTO;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.formateur.mapper.FormateurMapper;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.formateur.service.impl.FormateurServiceImpl;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormateurServiceImplTest {

    @Mock
    private FormateurRepository formateurRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private FormateurMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FormateurServiceImpl formateurService;

    private FormateurDTO dto;

    @BeforeEach
    void setUp() {
        dto = new FormateurDTO();
        dto.setNomPrenom("Ahmed Ben Ali");
        dto.setSpecialite("Informatique");
        dto.setDirection("DACA");
        dto.setEntreprise("CNI");
        dto.setEmail("ahmed@cni.tn");
        dto.setMotDePasse("motdepasse123");
    }

    @Test
    void creer_devraitLeverExceptionSiEmailExisteDeja() {
        when(utilisateurRepository.existsByEmail("ahmed@cni.tn")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> formateurService.creer(dto));

        assertEquals("البريد الإلكتروني مستعمل بالفعل", exception.getMessage());
        verify(formateurRepository, never()).save(any());
    }

    @Test
    void creer_devraitReussirSiEmailNexistePas() {
        when(utilisateurRepository.existsByEmail("ahmed@cni.tn")).thenReturn(false);
        when(passwordEncoder.encode("motdepasse123")).thenReturn("hash_simule");
        when(formateurRepository.save(any(Formateur.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(Formateur.class))).thenReturn(dto);

        FormateurDTO resultat = formateurService.creer(dto);

        assertNotNull(resultat);
        verify(formateurRepository, times(1)).save(any(Formateur.class));
    }

    @Test
    void trouverParId_devraitLeverExceptionSiIntrouvable() {
        when(formateurRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> formateurService.trouverParId(99L));
    }

    @Test
    void supprimer_devraitLeverExceptionSiIntrouvable() {
        when(formateurRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> formateurService.supprimer(99L));
        verify(formateurRepository, never()).deleteById(any());
    }

    @Test
    void supprimer_devraitReussirSiExiste() {
        when(formateurRepository.existsById(1L)).thenReturn(true);

        formateurService.supprimer(1L);

        verify(formateurRepository, times(1)).deleteById(1L);
    }
}