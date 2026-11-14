package tn.enicarthage.gestionformation.inscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
import tn.enicarthage.gestionformation.inscription.entity.Inscription;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByParticipant_Id(Long participantId);
    List<Inscription> findByParticipant_IdAndStatut(Long participantId, StatutInscription statut);
    List<Inscription> findByCycle_IdAndStatut(Long cycleId, StatutInscription statut);
    Optional<Inscription> findByParticipant_IdAndCycle_IdAndStatut(Long participantId, Long cycleId, StatutInscription statut);
    long countByCycle_IdAndStatut(Long cycleId, StatutInscription statut);
}