package tn.enicarthage.gestionformation.presence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.gestionformation.presence.entity.PresenceSeance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresenceSeanceRepository extends JpaRepository<PresenceSeance, Long> {
    Optional<PresenceSeance> findByParticipant_IdAndCycle_IdAndDateSeance(Long participantId, Long cycleId, LocalDate date);
    List<PresenceSeance> findByParticipant_IdAndCycle_Id(Long participantId, Long cycleId);
    List<PresenceSeance> findByParticipant_Id(Long participantId);
    List<PresenceSeance> findByCycle_Id(Long cycleId);
    long countByParticipant_IdAndCycle_IdAndPresentFalseAndJustifieFalse(Long participantId, Long cycleId);
}