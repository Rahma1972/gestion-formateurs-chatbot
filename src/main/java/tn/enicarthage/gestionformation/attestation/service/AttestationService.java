package tn.enicarthage.gestionformation.attestation.service;

public interface AttestationService {
    boolean estEligible(Long participantId, Long cycleId);
    byte[] genererAttestation(Long participantId, Long cycleId);
}