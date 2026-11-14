package tn.enicarthage.gestionformation.evaluation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.evaluation.entity.Evaluation;
import tn.enicarthage.gestionformation.evaluation.repository.EvaluationRepository;
import tn.enicarthage.gestionformation.evaluation.service.EvaluationService;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ParticipantRepository participantRepository;
    private final CycleRepository cycleRepository;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository, ParticipantRepository participantRepository,
                                 CycleRepository cycleRepository) {
        this.evaluationRepository = evaluationRepository;
        this.participantRepository = participantRepository;
        this.cycleRepository = cycleRepository;
    }

    @Override
    public boolean dejaEvalue(Long participantId, Long cycleId) {
        return evaluationRepository.findByParticipant_IdAndCycle_Id(participantId, cycleId).isPresent();
    }

    @Override
    @Transactional
    public void evaluer(Long participantId, Long cycleId, int note, String commentaire) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.participant.introuvable", participantId));
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        if (cycle.getDateFin().isAfter(LocalDate.now())) {
            throw new BusinessException("erreur.evaluation.cycleNonTermine");
        }
        if (dejaEvalue(participantId, cycleId)) {
            throw new BusinessException("erreur.evaluation.dejaEvalue");
        }
        if (note < 1 || note > 5) {
            throw new BusinessException("erreur.evaluation.noteInvalide");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setParticipant(participant);
        evaluation.setCycle(cycle);
        evaluation.setNote(note);
        evaluation.setCommentaire(commentaire);
        evaluationRepository.save(evaluation);
    }

    @Override
    public Double moyenneCycle(Long cycleId) {
        List<Evaluation> evaluations = evaluationRepository.findByCycle_Id(cycleId);
        if (evaluations.isEmpty()) return null;
        return evaluations.stream().mapToInt(Evaluation::getNote).average().orElse(0);
    }
}