package tn.enicarthage.gestionformation.inscription.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.inscription.entity.Inscription;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.inscription.service.impl.InscriptionServiceImpl;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.salle.entity.Salle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscriptionServiceImplTest {

    @Mock
    private InscriptionRepository inscriptionRepository;
    @Mock
    private CycleRepository cycleRepository;
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private InscriptionServiceImpl inscriptionService;

    private Participant participant;
    private Cycle cycleA;
    private Cycle cycleB;

    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setId(1L);

        Salle salle = new Salle();
        salle.setCapacite(10);

        cycleA = new Cycle();
        cycleA.setId(10L);
        cycleA.setDateDeb(LocalDate.of(2026, 6, 1));
        cycleA.setDateFin(LocalDate.of(2026, 6, 10));
        cycleA.setSalle(salle);

        cycleB = new Cycle();
        cycleB.setId(20L);
        cycleB.setSalle(salle);
    }

    @Test
    void sInscrire_devraitReussirSiPasDeConflit() {
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(cycleRepository.findById(10L)).thenReturn(Optional.of(cycleA));
        when(inscriptionRepository.findByParticipant_IdAndCycle_IdAndStatut(1L, 10L, StatutInscription.ACTIVE))
                .thenReturn(Optional.empty());
        when(inscriptionRepository.countByCycle_IdAndStatut(10L, StatutInscription.ACTIVE)).thenReturn(0L);
        when(inscriptionRepository.findByParticipant_IdAndStatut(1L, StatutInscription.ACTIVE)).thenReturn(List.of());

        assertDoesNotThrow(() -> inscriptionService.sInscrire(1L, 10L));
        verify(inscriptionRepository, times(1)).save(any(Inscription.class));
    }

    @Test
    void sInscrire_devraitEchouerSiCycleComplet() {
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(cycleRepository.findById(10L)).thenReturn(Optional.of(cycleA));
        when(inscriptionRepository.findByParticipant_IdAndCycle_IdAndStatut(1L, 10L, StatutInscription.ACTIVE))
                .thenReturn(Optional.empty());
        when(inscriptionRepository.countByCycle_IdAndStatut(10L, StatutInscription.ACTIVE)).thenReturn(10L); // déjà plein

        BusinessException exception = assertThrows(BusinessException.class,
                () -> inscriptionService.sInscrire(1L, 10L));
        assertEquals("erreur.inscription.completLe", exception.getMessage());
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void sInscrire_devraitEchouerSiDejaInscrit() {
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(cycleRepository.findById(10L)).thenReturn(Optional.of(cycleA));

        Inscription existante = new Inscription();
        when(inscriptionRepository.findByParticipant_IdAndCycle_IdAndStatut(1L, 10L, StatutInscription.ACTIVE))
                .thenReturn(Optional.of(existante));

        assertThrows(BusinessException.class, () -> inscriptionService.sInscrire(1L, 10L));
    }

    @Test
    void sInscrire_devraitEchouerSiChevauchementDeDates() {
        cycleB.setDateDeb(LocalDate.of(2026, 6, 5)); // chevauche cycleA (1-10 juin)
        cycleB.setDateFin(LocalDate.of(2026, 6, 15));

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(cycleRepository.findById(20L)).thenReturn(Optional.of(cycleB));
        when(inscriptionRepository.findByParticipant_IdAndCycle_IdAndStatut(1L, 20L, StatutInscription.ACTIVE))
                .thenReturn(Optional.empty());
        when(inscriptionRepository.countByCycle_IdAndStatut(20L, StatutInscription.ACTIVE)).thenReturn(0L);

        Inscription inscriptionExistante = new Inscription();
        inscriptionExistante.setCycle(cycleA);
        when(inscriptionRepository.findByParticipant_IdAndStatut(1L, StatutInscription.ACTIVE))
                .thenReturn(List.of(inscriptionExistante));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> inscriptionService.sInscrire(1L, 20L));
        assertEquals("erreur.inscription.chevauchement", exception.getMessage());
    }

    @Test
    void sInscrire_devraitReussirSiDatesNeChevauchentPas() {
        cycleB.setDateDeb(LocalDate.of(2026, 7, 1)); // après cycleA, pas de chevauchement
        cycleB.setDateFin(LocalDate.of(2026, 7, 10));

        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));
        when(cycleRepository.findById(20L)).thenReturn(Optional.of(cycleB));
        when(inscriptionRepository.findByParticipant_IdAndCycle_IdAndStatut(1L, 20L, StatutInscription.ACTIVE))
                .thenReturn(Optional.empty());
        when(inscriptionRepository.countByCycle_IdAndStatut(20L, StatutInscription.ACTIVE)).thenReturn(0L);

        Inscription inscriptionExistante = new Inscription();
        inscriptionExistante.setCycle(cycleA);
        when(inscriptionRepository.findByParticipant_IdAndStatut(1L, StatutInscription.ACTIVE))
                .thenReturn(List.of(inscriptionExistante));

        assertDoesNotThrow(() -> inscriptionService.sInscrire(1L, 20L));
    }
}