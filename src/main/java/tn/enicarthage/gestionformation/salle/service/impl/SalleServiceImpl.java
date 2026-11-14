package tn.enicarthage.gestionformation.salle.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.salle.dto.SalleDTO;
import tn.enicarthage.gestionformation.salle.entity.Salle;
import tn.enicarthage.gestionformation.salle.mapper.SalleMapper;
import tn.enicarthage.gestionformation.salle.repository.SalleRepository;
import tn.enicarthage.gestionformation.salle.service.SalleService;

import java.util.List;

@Service
public class SalleServiceImpl implements SalleService {

    private final SalleRepository salleRepository;
    private final CycleRepository cycleRepository;
    private final SalleMapper mapper;

    public SalleServiceImpl(SalleRepository salleRepository, CycleRepository cycleRepository, SalleMapper mapper) {
        this.salleRepository = salleRepository;
        this.cycleRepository = cycleRepository;
        this.mapper = mapper;
    }

    @Override
    public List<SalleDTO> listerTous() {
        return salleRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public SalleDTO trouverParId(Long id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.salle.introuvable" + id));
        return mapper.toDTO(salle);
    }

    @Override
    @Transactional
    public SalleDTO creer(SalleDTO dto) {
        Salle salle = new Salle();
        mapper.mettreAJourEntite(salle, dto);
        return mapper.toDTO(salleRepository.save(salle));
    }

    @Override
    @Transactional
    public SalleDTO modifier(Long id, SalleDTO dto) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.salle.introuvable" + id));
        mapper.mettreAJourEntite(salle, dto);
        return mapper.toDTO(salleRepository.save(salle));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!salleRepository.existsById(id)) {
            throw new ResourceNotFoundException("erreur.salle.introuvable" + id);
        }
        if (cycleRepository.existsBySalle_Id(id)) {
            throw new BusinessException("erreur.salle.utiliseeParCycle");
        }
        salleRepository.deleteById(id);
    }
}