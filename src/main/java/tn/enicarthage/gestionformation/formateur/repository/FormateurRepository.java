package tn.enicarthage.gestionformation.formateur.repository;

import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {
}