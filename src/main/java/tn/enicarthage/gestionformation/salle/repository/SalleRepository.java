package tn.enicarthage.gestionformation.salle.repository;

import tn.enicarthage.gestionformation.salle.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, Long> {
}