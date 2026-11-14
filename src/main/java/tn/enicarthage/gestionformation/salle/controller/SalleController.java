package tn.enicarthage.gestionformation.salle.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.salle.dto.SalleDTO;
import tn.enicarthage.gestionformation.salle.service.SalleService;

@Controller
@RequestMapping("/salles")
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String recherche, Model model) {
        var salles = salleService.listerTous();
        if (recherche != null && !recherche.isBlank()) {
            salles = salles.stream()
                    .filter(s -> s.getNom().toLowerCase().contains(recherche.toLowerCase()))
                    .toList();
        }
        model.addAttribute("salles", salles);
        model.addAttribute("recherche", recherche);
        return "salle/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireAjout(Model model) {
        model.addAttribute("salle", new SalleDTO());
        model.addAttribute("modeModification", false);
        return "salle/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("salle") SalleDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("modeModification", false);
            return "salle/formulaire";
        }
        salleService.creer(dto);
        return "redirect:/salles";
    }

    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("salle", salleService.trouverParId(id));
        model.addAttribute("modeModification", true);
        return "salle/formulaire";
    }

    @PostMapping("/modifier/{id}")
    public String modifier(@PathVariable Long id, @Valid @ModelAttribute("salle") SalleDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("modeModification", true);
            return "salle/formulaire";
        }
        salleService.modifier(id, dto);
        return "redirect:/salles";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        salleService.supprimer(id);
        return "redirect:/salles";
    }
}