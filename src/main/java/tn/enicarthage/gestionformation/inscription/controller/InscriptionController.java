package tn.enicarthage.gestionformation.inscription.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.enicarthage.gestionformation.inscription.service.InscriptionService;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.security.CustomUserDetails;

@Controller
@RequestMapping("/mes-formations")
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final ParticipantRepository participantRepository;

    public InscriptionController(InscriptionService inscriptionService, ParticipantRepository participantRepository) {
        this.inscriptionService = inscriptionService;
        this.participantRepository = participantRepository;
    }

    @GetMapping
    public String mesFormations(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long participantId = trouverParticipantId(userDetails);
        model.addAttribute("cyclesDisponibles", inscriptionService.listerCyclesDisponibles(participantId));
        model.addAttribute("historique", inscriptionService.listerHistorique(participantId));
        return "inscription/mes-formations";
    }

    @PostMapping("/inscrire/{cycleId}")
    public String sInscrire(@PathVariable Long cycleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long participantId = trouverParticipantId(userDetails);
        inscriptionService.sInscrire(participantId, cycleId);
        return "redirect:/mes-formations";
    }

    @PostMapping("/annuler/{inscriptionId}")
    public String annuler(@PathVariable Long inscriptionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long participantId = trouverParticipantId(userDetails);
        inscriptionService.annuler(participantId, inscriptionId);
        return "redirect:/mes-formations";
    }

    private Long trouverParticipantId(CustomUserDetails userDetails) {
        return participantRepository.findAll().stream()
                .filter(p -> p.getCompte() != null && p.getCompte().getEmail().equals(userDetails.getUsername()))
                .findFirst().orElseThrow().getId();
    }
}