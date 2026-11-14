package tn.enicarthage.gestionformation.evaluation.service;

public interface EvaluationService {
    boolean dejaEvalue(Long participantId, Long cycleId);
    void evaluer(Long participantId, Long cycleId, int note, String commentaire);
    Double moyenneCycle(Long cycleId);
}