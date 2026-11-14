package tn.enicarthage.gestionformation.evaluation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.gestionformation.evaluation.entity.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByParticipant_IdAndCycle_Id(Long participantId, Long cycleId);
    List<Evaluation> findByCycle_Id(Long cycleId);
}