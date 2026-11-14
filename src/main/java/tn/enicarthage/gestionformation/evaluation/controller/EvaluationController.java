package tn.enicarthage.gestionformation.evaluation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.evaluation.service.EvaluationService;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.security.CustomUserDetails;

@Controller
@RequestMapping("/mes-formations/evaluer")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final ParticipantRepository participantRepository;

    public EvaluationController(EvaluationService evaluationService, ParticipantRepository participantRepository) {
        this.evaluationService = evaluationService;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/{cycleId}")
    public String formulaire(@PathVariable Long cycleId, Model model) {
        model.addAttribute("cycleId", cycleId);
        return "evaluation/formulaire";
    }

    @PostMapping("/{cycleId}")
    public String evaluer(@PathVariable Long cycleId, @RequestParam int note, @RequestParam(required = false) String commentaire,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        var participant = participantRepository.findAll().stream()
                .filter(p -> p.getCompte() != null && p.getCompte().getEmail().equals(userDetails.getUsername()))
                .findFirst().orElseThrow();

        evaluationService.evaluer(participant.getId(), cycleId, note, commentaire);
        return "redirect:/mes-formations";
    }
}