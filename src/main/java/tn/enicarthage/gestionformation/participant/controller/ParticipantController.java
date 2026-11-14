package tn.enicarthage.gestionformation.participant.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.inscription.service.InscriptionService;
import tn.enicarthage.gestionformation.participant.dto.ParticipantDTO;
import tn.enicarthage.gestionformation.participant.service.ParticipantService;
import tn.enicarthage.gestionformation.security.CustomUserDetails;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class ParticipantController {

    private final ParticipantService participantService;
    private final InscriptionService inscriptionService;

    public ParticipantController(ParticipantService participantService, InscriptionService inscriptionService) {
        this.participantService = participantService;
        this.inscriptionService = inscriptionService;
    }

    @GetMapping("/inscription")
    public String formulaireInscription(Model model) {
        model.addAttribute("participant", new ParticipantDTO());
        return "participant/inscription";
    }

    @PostMapping("/inscription")
    @Transactional
    public String inscrire(@Valid @ModelAttribute("participant") ParticipantDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "participant/inscription";
        }
        ParticipantDTO cree = participantService.creer(dto);
        return "participant/inscription-succes";
    }
    @GetMapping("/participants")
    public String liste(@RequestParam(required = false) String recherche,
                        @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean estAdmin = userDetails.getUtilisateur().getRole().name().equals("ADMIN");
        var participants = estAdmin
                ? participantService.listerTous()
                : participantService.listerTous().stream()
                .filter(p -> p.getEmail().equals(userDetails.getUsername()))
                .toList();

        if (recherche != null && !recherche.isBlank()) {
            participants = participants.stream()
                    .filter(p -> p.getNomPrenom().toLowerCase().contains(recherche.toLowerCase()))
                    .toList();
        }

        model.addAttribute("participants", participants);
        model.addAttribute("recherche", recherche);
        return "participant/liste";
    }

    @GetMapping("/participants/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        verifierProprietaire(id, userDetails);
        model.addAttribute("participant", participantService.trouverParId(id));
        model.addAttribute("modeModification", true);
        return "participant/formulaire";
    }

    @PostMapping("/participants/modifier/{id}")
    public String modifier(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
                           @Valid @ModelAttribute("participant") ParticipantDTO dto, BindingResult result, Model model) {
        verifierProprietaire(id, userDetails);
        if (result.hasErrors()) {
            model.addAttribute("modeModification", true);
            return "participant/formulaire";
        }
        participantService.modifier(id, dto);
        return "redirect:/participants";
    }

    @GetMapping("/participants/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        participantService.supprimer(id);
        return "redirect:/participants";
    }

    private void verifierProprietaire(Long idDemande, CustomUserDetails userDetails) {
        boolean estAdmin = userDetails.getUtilisateur().getRole().name().equals("ADMIN");
        if (estAdmin) return;

        ParticipantDTO proprietaire = participantService.trouverParId(idDemande);
        boolean estProprietaire = proprietaire.getEmail().equals(userDetails.getUsername());
        if (!estProprietaire) {
            throw new AccessDeniedException("erreur.acces.participant");
        }
    }
}