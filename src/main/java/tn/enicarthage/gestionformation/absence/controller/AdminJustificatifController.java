package tn.enicarthage.gestionformation.absence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.absence.service.JustificatifService;

@Controller
@RequestMapping("/admin/justificatifs")
public class AdminJustificatifController {

    private final JustificatifService justificatifService;

    public AdminJustificatifController(JustificatifService justificatifService) {
        this.justificatifService = justificatifService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("justificatifs", justificatifService.listerTout());
        return "absence/admin-justificatifs";
    }

    @PostMapping("/{id}/traiter")
    public String traiter(@PathVariable Long id, @RequestParam boolean accepter,
                          @RequestParam(required = false) String commentaire) {
        justificatifService.traiter(id, accepter, commentaire);
        return "redirect:/admin/justificatifs";
    }
}