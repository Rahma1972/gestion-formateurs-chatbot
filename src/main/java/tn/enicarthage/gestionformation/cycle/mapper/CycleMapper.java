package tn.enicarthage.gestionformation.cycle.mapper;

import org.springframework.stereotype.Component;
import tn.enicarthage.gestionformation.common.enums.StatutCycle;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.dto.CycleDTO;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.salle.entity.Salle;
import tn.enicarthage.gestionformation.salle.repository.SalleRepository;

import java.util.HashSet;
import java.util.List;

import java.util.stream.Collectors;

@Component
public class CycleMapper {

    private final SalleRepository salleRepository;
    private final FormateurRepository formateurRepository;

    public CycleMapper(SalleRepository salleRepository, FormateurRepository formateurRepository) {
        this.salleRepository = salleRepository;
        this.formateurRepository = formateurRepository;
    }

    public CycleDTO toDTO(Cycle cycle) {
        CycleDTO dto = new CycleDTO();
        dto.setId(cycle.getId());
        dto.setNumAct(cycle.getNumAct());
        dto.setTheme(cycle.getTheme());
        dto.setDateDeb(cycle.getDateDeb());
        dto.setDateFin(cycle.getDateFin());
        dto.setStatut(calculerStatutAffiche(cycle));


        if (cycle.getSalle() != null) {
            dto.setSalleId(cycle.getSalle().getId());
            dto.setSalleNom(cycle.getSalle().getNom());
        }

        dto.setFormateurIds(
                cycle.getFormateurs().stream().map(Formateur::getId).collect(Collectors.toList())
        );
        dto.setFormateurNoms(
                cycle.getFormateurs().stream().map(Formateur::getNomPrenom).collect(Collectors.toList())
        );

        return dto;
    }

    public void mettreAJourEntite(Cycle cycle, CycleDTO dto) {
        cycle.setNumAct(dto.getNumAct());
        cycle.setTheme(dto.getTheme());
        cycle.setDateDeb(dto.getDateDeb());
        cycle.setDateFin(dto.getDateFin());


        if (dto.getStatut() != null) {
            cycle.setStatut(StatutCycle.valueOf(dto.getStatut()));
        }

        Salle salle = salleRepository.findById(dto.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("القاعة غير موجودة، المعرف = " + dto.getSalleId()));
        cycle.setSalle(salle);

        List<Formateur> formateurs = formateurRepository.findAllById(dto.getFormateurIds());
        cycle.setFormateurs(new HashSet<>(formateurs));
    }
    private String calculerStatutAffiche(Cycle cycle) {
        // Le statut ANNULE reste toujours manuel, jamais recalculé
        if (cycle.getStatut() == tn.enicarthage.gestionformation.common.enums.StatutCycle.ANNULE) {
            return "ANNULE";
        }

        java.time.LocalDate aujourdHui = java.time.LocalDate.now();
        if (aujourdHui.isBefore(cycle.getDateDeb())) {
            return "PLANIFIE";
        } else if (aujourdHui.isAfter(cycle.getDateFin())) {
            return "TERMINE";
        } else {
            return "EN_COURS";
        }
    }
}