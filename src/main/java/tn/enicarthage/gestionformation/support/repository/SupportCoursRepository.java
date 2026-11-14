package tn.enicarthage.gestionformation.support.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.gestionformation.support.entity.SupportCours;

import java.util.List;

public interface SupportCoursRepository extends JpaRepository<SupportCours, Long> {
    List<SupportCours> findByCycle_Id(Long cycleId);
}