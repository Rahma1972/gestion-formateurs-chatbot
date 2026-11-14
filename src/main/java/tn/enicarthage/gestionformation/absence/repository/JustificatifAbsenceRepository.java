package tn.enicarthage.gestionformation.absence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.gestionformation.absence.entity.JustificatifAbsence;

import java.util.List;
import java.util.Optional;

public interface JustificatifAbsenceRepository extends JpaRepository<JustificatifAbsence, Long> {
    Optional<JustificatifAbsence> findByPresenceSeance_Id(Long presenceSeanceId);
    List<JustificatifAbsence> findAllByOrderByDateDepotDesc();
}