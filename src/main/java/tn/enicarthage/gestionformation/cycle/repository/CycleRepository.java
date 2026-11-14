package tn.enicarthage.gestionformation.cycle.repository;

import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CycleRepository extends JpaRepository<Cycle, Long> {
    boolean existsBySalle_Id(Long salleId);
}