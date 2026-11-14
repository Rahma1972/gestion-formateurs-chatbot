package tn.enicarthage.gestionformation.cycle.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.dto.CycleDTO;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.mapper.CycleMapper;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.cycle.service.CycleService;
import tn.enicarthage.gestionformation.salle.repository.SalleRepository;

import java.util.List;

@Service
public class CycleServiceImpl implements CycleService {

    private final CycleRepository cycleRepository;
    private final CycleMapper mapper;
    private final SalleRepository salleRepository;
    public CycleServiceImpl(CycleRepository cycleRepository, CycleMapper mapper , SalleRepository salleRepository) {
        this.cycleRepository = cycleRepository;
        this.mapper = mapper;
        this.salleRepository = salleRepository;
    }

    @Override
    public List<CycleDTO> listerTous() {
        return cycleRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public CycleDTO trouverParId(Long id) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", id));
        return mapper.toDTO(cycle);
    }

    @Override
    @Transactional
    public CycleDTO creer(CycleDTO dto) {
        verifierCoherenceDates(dto);

        dto.setNumAct(genererNumAct());
        Cycle cycle = new Cycle();
        mapper.mettreAJourEntite(cycle, dto);
        return mapper.toDTO(cycleRepository.save(cycle));
    }

    @Override
    @Transactional
    public CycleDTO modifier(Long id, CycleDTO dto) {
        verifierCoherenceDates(dto);

        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable" + id));
        String numActExistant = cycle.getNumAct(); // on sauvegarde la valeur actuelle avant modification
        mapper.mettreAJourEntite(cycle, dto);
        cycle.setNumAct(numActExistant); // on la restaure explicitement, au cas où
        return mapper.toDTO(cycleRepository.save(cycle));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!cycleRepository.existsById(id)) {
            throw new ResourceNotFoundException("erreur.cycle.introuvable" + id);
        }
        cycleRepository.deleteById(id);
    }

    private void verifierCoherenceDates(CycleDTO dto) {
        if (dto.getDateDeb() != null && dto.getDateFin() != null
                && dto.getDateFin().isBefore(dto.getDateDeb())) {
            throw new BusinessException("erreur.cycle.datesIncoherentes");
        }
    }


    private String genererNumAct() {
        long compteur = cycleRepository.count() + 1;
        int annee = java.time.LocalDate.now().getYear();
        return compteur + "/" + annee;
    }
}