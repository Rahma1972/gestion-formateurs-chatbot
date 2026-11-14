package tn.enicarthage.gestionformation.attestation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicarthage.gestionformation.attestation.service.impl.AttestationServiceImpl;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.presence.entity.PresenceSeance;
import tn.enicarthage.gestionformation.presence.repository.PresenceSeanceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttestationServiceImplTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private CycleRepository cycleRepository;
    @Mock
    private PresenceSeanceRepository presenceSeanceRepository;

    @InjectMocks
    private AttestationServiceImpl attestationService;

    private Cycle cycleTermine;

    @BeforeEach
    void setUp() {
        cycleTermine = new Cycle();
        cycleTermine.setId(1L);
        cycleTermine.setDateFin(LocalDate.now().minusDays(5)); // cycle déjà terminé
    }

    private PresenceSeance creerSeance(Long participantId, boolean present, boolean justifie) {
        PresenceSeance seance = new PresenceSeance();
        Participant p = new Participant();
        p.setId(participantId);
        seance.setParticipant(p);
        seance.setPresent(present);
        seance.setJustifie(justifie);
        return seance;
    }

    @Test
    void estEligible_devraitEtreFauxSiCycleNonTermine() {
        Cycle cycleEnCours = new Cycle();
        cycleEnCours.setId(2L);
        cycleEnCours.setDateFin(LocalDate.now().plusDays(5)); // pas encore terminé

        when(cycleRepository.findById(2L)).thenReturn(Optional.of(cycleEnCours));

        assertFalse(attestationService.estEligible(1L, 2L));
    }

    @Test
    void estEligible_devraitEtreVraiSiAucuneAbsence() {
        when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycleTermine));
        when(presenceSeanceRepository.findByCycle_Id(1L)).thenReturn(List.of(
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false)
        ));
        when(presenceSeanceRepository.findByParticipant_IdAndCycle_Id(1L, 1L)).thenReturn(List.of(
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false)
        ));

        assertTrue(attestationService.estEligible(1L, 1L));
    }

    @Test
    void estEligible_devraitEtreFauxSiTropDAbsencesNonJustifiees() {
        // 5 séances, 2 absences non justifiées = 40% > seuil de 20%
        when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycleTermine));
        when(presenceSeanceRepository.findByCycle_Id(1L)).thenReturn(List.of(
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, false, false),
                creerSeance(1L, false, false)
        ));
        when(presenceSeanceRepository.findByParticipant_IdAndCycle_Id(1L, 1L)).thenReturn(List.of(
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, false, false),
                creerSeance(1L, false, false)
        ));

        assertFalse(attestationService.estEligible(1L, 1L));
    }

    @Test
    void estEligible_devraitEtreVraiSiAbsenceJustifiee() {
        // 5 séances, 1 absence mais JUSTIFIÉE = ne compte pas dans le taux
        when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycleTermine));
        when(presenceSeanceRepository.findByCycle_Id(1L)).thenReturn(List.of(
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, false, true) // absence justifiée
        ));
        when(presenceSeanceRepository.findByParticipant_IdAndCycle_Id(1L, 1L)).thenReturn(List.of(
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, true, false),
                creerSeance(1L, false, true)
        ));

        assertTrue(attestationService.estEligible(1L, 1L));
    }

    @Test
    void genererAttestation_devraitEchouerSiNonEligible() {
        Cycle cycleEnCours = new Cycle();
        cycleEnCours.setId(2L);
        cycleEnCours.setDateFin(LocalDate.now().plusDays(5));

        when(cycleRepository.findById(2L)).thenReturn(Optional.of(cycleEnCours));

        assertThrows(BusinessException.class, () -> attestationService.genererAttestation(1L, 2L));
    }
}