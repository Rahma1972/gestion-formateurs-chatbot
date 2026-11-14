package tn.enicarthage.gestionformation.attestation.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import tn.enicarthage.gestionformation.attestation.service.AttestationService;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.security.CustomUserDetails;

@Controller
public class AttestationController {

    private final AttestationService attestationService;
    private final ParticipantRepository participantRepository;

    public AttestationController(AttestationService attestationService, ParticipantRepository participantRepository) {
        this.attestationService = attestationService;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/mes-formations/attestation/{cycleId}")
    @ResponseBody
    public ResponseEntity<byte[]> telechargerAttestation(@PathVariable Long cycleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        var participant = participantRepository.findAll().stream()
                .filter(p -> p.getCompte() != null && p.getCompte().getEmail().equals(userDetails.getUsername()))
                .findFirst().orElseThrow();

        byte[] pdf = attestationService.genererAttestation(participant.getId(), cycleId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attestation.pdf\"")
                .body(pdf);
    }
}