package tn.enicarthage.gestionformation.formateur.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.formateur.dto.FormateurDTO;
import tn.enicarthage.gestionformation.formateur.service.FormateurService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import tn.enicarthage.gestionformation.security.CustomUserDetails;


@Controller
@RequestMapping("/formateurs")
public class FormateurController {

    private final FormateurService formateurService;

    public FormateurController(FormateurService formateurService) {
        this.formateurService = formateurService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String recherche,
                        @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean estAdmin = userDetails.getUtilisateur().getRole().name().equals("ADMIN");
        var formateurs = estAdmin
                ? formateurService.listerTous()
                : formateurService.listerTous().stream()
                .filter(f -> f.getEmail().equals(userDetails.getUsername()))
                .toList();

        if (recherche != null && !recherche.isBlank()) {
            formateurs = formateurs.stream()
                    .filter(f -> f.getNomPrenom().toLowerCase().contains(recherche.toLowerCase()))
                    .toList();
        }

        model.addAttribute("formateurs", formateurs);
        model.addAttribute("recherche", recherche);
        return "formateur/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireAjout(Model model) {
        model.addAttribute("formateur", new FormateurDTO());
        return "formateur/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("formateur") FormateurDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return "formateur/formulaire";
        }
        formateurService.creer(dto);
        return "redirect:/formateurs";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        verifierProprietaire(id, userDetails);
        formateurService.supprimer(id);
        return "redirect:/formateurs";
    }
    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        verifierProprietaire(id, userDetails);
        model.addAttribute("formateur", formateurService.trouverParId(id));
        model.addAttribute("modeModification", true);
        return "formateur/formulaire";
    }

    @PostMapping("/modifier/{id}")
    public String modifier(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
                           @Valid @ModelAttribute("formateur") FormateurDTO dto, BindingResult result, Model model) {
        verifierProprietaire(id, userDetails);
        if (result.hasErrors()) {
            model.addAttribute("modeModification", true);
            return "formateur/formulaire";
        }
        formateurService.modifier(id, dto);
        return "redirect:/formateurs";
    }
    private void verifierProprietaire(Long idDemande, CustomUserDetails userDetails) {
        boolean estAdmin = userDetails.getUtilisateur().getRole().name().equals("ADMIN");
        if (estAdmin) return;

        FormateurDTO proprietaire = formateurService.trouverParId(idDemande);
        boolean estProprietaire = proprietaire.getEmail().equals(userDetails.getUsername());
        if (!estProprietaire) {
            throw new AccessDeniedException("erreur.acces.formateur");
        }
    }
}