package tn.enicarthage.gestionformation.utilisateur.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.audit.service.AuditService;
import tn.enicarthage.gestionformation.utilisateur.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.security.CustomUserDetails;
import tn.enicarthage.gestionformation.utilisateur.dto.ChangementMotDePasseDTO;

@Controller
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final AuditService auditService;

    public UtilisateurController(UtilisateurService utilisateurService, AuditService auditService) {
        this.utilisateurService = utilisateurService;
        this.auditService = auditService;
    }

    @GetMapping("/admin/utilisateurs")
    public String comptesEnAttente(Model model) {
        model.addAttribute("comptes", utilisateurService.listerComptesEnAttente());
        return "utilisateur/comptes-attente";
    }

    @GetMapping("/admin/utilisateurs/activer/{id}")
    public String activer(@PathVariable Long id) {
        utilisateurService.activerCompte(id);
        return "redirect:/admin/utilisateurs";
    }

    @GetMapping("/admin/utilisateurs/journal")
    public String journal(Model model) {
        model.addAttribute("evenements", auditService.listerTout());
        return "utilisateur/journal";
    }

    @GetMapping("/mon-compte/mot-de-passe")
    public String formulaireChangementMotDePasse(Model model) {
        model.addAttribute("motDePasse", new ChangementMotDePasseDTO());
        return "utilisateur/changer-mot-de-passe";
    }

    @PostMapping("/mon-compte/mot-de-passe")
    public String changerMotDePasse(@Valid @ModelAttribute("motDePasse") ChangementMotDePasseDTO dto,
                                    BindingResult result,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model) {
        if (result.hasErrors()) {
            return "utilisateur/changer-mot-de-passe";
        }
        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
            model.addAttribute("erreurConfirmation", "كلمتا العبور غير متطابقتين");
            return "utilisateur/changer-mot-de-passe";
        }
        try {
            utilisateurService.changerMotDePasse(userDetails.getUsername(), dto.getMotDePasseActuel(), dto.getNouveauMotDePasse());
        } catch (BusinessException e) {
            model.addAttribute("erreurConfirmation", e.getMessage());
            return "utilisateur/changer-mot-de-passe";
        }
        model.addAttribute("succes", true);
        return "utilisateur/changer-mot-de-passe";
    }
}