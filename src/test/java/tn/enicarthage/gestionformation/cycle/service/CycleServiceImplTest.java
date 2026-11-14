package tn.enicarthage.gestionformation.cycle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.dto.CycleDTO;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.mapper.CycleMapper;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.cycle.service.impl.CycleServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CycleServiceImplTest {

    @Mock
    private CycleRepository cycleRepository;

    @Mock
    private CycleMapper mapper;

    @InjectMocks
    private CycleServiceImpl cycleService;

    private CycleDTO dto;

    @BeforeEach
    void setUp() {
        dto = new CycleDTO();
        dto.setTheme("تكوين في أمن المعلومات");
        dto.setDateDeb(LocalDate.of(2026, 3, 10));
        dto.setDateFin(LocalDate.of(2026, 3, 15));
        dto.setSalleId(1L);
        dto.setFormateurIds(List.of(1L, 2L));
    }

    @Test
    void creer_devraitLeverExceptionSiDateFinAvantDateDebut() {
        dto.setDateDeb(LocalDate.of(2026, 3, 15));
        dto.setDateFin(LocalDate.of(2026, 3, 10)); // fin avant début

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cycleService.creer(dto));

        assertEquals("تاريخ النهاية يجب أن يكون بعد تاريخ البداية", exception.getMessage());
        verify(cycleRepository, never()).save(any());
    }

    @Test
    void creer_devraitReussirSiDatesCoherentes() {
        when(cycleRepository.count()).thenReturn(0L);
        when(cycleRepository.save(any(Cycle.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(Cycle.class))).thenReturn(dto);

        CycleDTO resultat = cycleService.creer(dto);

        assertNotNull(resultat);
        verify(cycleRepository, times(1)).save(any(Cycle.class));
    }

    @Test
    void creer_devraitAccepterDatesIdentiques() {
        dto.setDateDeb(LocalDate.of(2026, 3, 10));
        dto.setDateFin(LocalDate.of(2026, 3, 10)); // même jour, cas limite valide

        when(cycleRepository.count()).thenReturn(0L);
        when(cycleRepository.save(any(Cycle.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(Cycle.class))).thenReturn(dto);

        assertDoesNotThrow(() -> cycleService.creer(dto));
    }

    @Test
    void trouverParId_devraitLeverExceptionSiIntrouvable() {
        when(cycleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cycleService.trouverParId(99L));
    }

    @Test
    void modifier_devraitLeverExceptionSiDateFinAvantDateDebut() {
        dto.setDateDeb(LocalDate.of(2026, 3, 15));
        dto.setDateFin(LocalDate.of(2026, 3, 10));

        assertThrows(BusinessException.class,
                () -> cycleService.modifier(1L, dto));

        verify(cycleRepository, never()).findById(any());
    }
}