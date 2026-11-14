package tn.enicarthage.gestionformation.presence.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.presence.service.PresenceService;
import tn.enicarthage.gestionformation.security.CustomUserDetails;
import java.time.LocalDate;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/formateurs/mes-cycles")
public class GestionCycleController {

    private final PresenceService presenceService;
    private final CycleRepository cycleRepository;

    public GestionCycleController(PresenceService presenceService, CycleRepository cycleRepository) {
        this.presenceService = presenceService;
        this.cycleRepository = cycleRepository;
    }

    @GetMapping
    public String mesCycles(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        var cycles = cycleRepository.findAll().stream()
                .filter(c -> c.getFormateurs().stream().anyMatch(f -> f.getCompte() != null && f.getCompte().getEmail().equals(email)))
                .toList();
        model.addAttribute("cycles", cycles);
        return "presence/mes-cycles";
    }

    @GetMapping("/{cycleId}")
    public String gererCycle(@PathVariable Long cycleId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        verifierAcces(cycleId, userDetails);
        model.addAttribute("gestion", presenceService.chargerGestionCycle(cycleId));
        return "presence/gestion-cycle";
    }

    @GetMapping("/{cycleId}/seance")
    public String gererSeance(@PathVariable Long cycleId,
                              @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
                              @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        verifierAcces(cycleId, userDetails);
        if (!date.isEqual(LocalDate.now())) {
            throw new BusinessException("erreur.seance.dateInvalide");
        }
        model.addAttribute("seance", presenceService.chargerSeance(cycleId, date));
        model.addAttribute("cycleId", cycleId);
        return "presence/gestion-seance";
    }

    @PostMapping("/{cycleId}/seance")
    public String enregistrerSeance(@PathVariable Long cycleId,
                                    @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam(required = false) List<Long> presents,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        verifierAcces(cycleId, userDetails);
        if (!date.isEqual(LocalDate.now())) {
            throw new BusinessException("erreur.seance.dateInvalide");
        }
        presenceService.enregistrerPresenceSeance(cycleId, date, presents != null ? presents : List.of());
        return "redirect:/formateurs/mes-cycles/" + cycleId;
    }

    @PostMapping("/{cycleId}/support")
    public String ajouterSupport(@PathVariable Long cycleId,
                                 @RequestParam String titre,
                                 @RequestParam String url,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        verifierAcces(cycleId, userDetails);
        presenceService.ajouterSupport(cycleId, titre, url);
        return "redirect:/formateurs/mes-cycles/" + cycleId;
    }

    @GetMapping("/support/supprimer/{supportId}/{cycleId}")
    public String supprimerSupport(@PathVariable Long supportId, @PathVariable Long cycleId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        verifierAcces(cycleId, userDetails);
        presenceService.supprimerSupport(supportId);
        return "redirect:/formateurs/mes-cycles/" + cycleId;
    }

    private void verifierAcces(Long cycleId, CustomUserDetails userDetails) {
        boolean estAdmin = userDetails.getUtilisateur().getRole().name().equals("ADMIN");
        if (estAdmin) return;

        String email = userDetails.getUsername();
        var cycle = cycleRepository.findById(cycleId).orElseThrow();
        boolean estFormateurDuCycle = cycle.getFormateurs().stream()
                .anyMatch(f -> f.getCompte() != null && f.getCompte().getEmail().equals(email));
        if (!estFormateurDuCycle) {
            throw new AccessDeniedException("erreur.acces.cycle");
        }
    }
}