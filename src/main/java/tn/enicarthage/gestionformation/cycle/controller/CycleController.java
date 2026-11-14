package tn.enicarthage.gestionformation.cycle.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.cycle.dto.CycleDTO;
import tn.enicarthage.gestionformation.cycle.service.CycleService;
import tn.enicarthage.gestionformation.formateur.service.FormateurService;
import tn.enicarthage.gestionformation.salle.repository.SalleRepository;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
@Controller
@RequestMapping("/cycles")
public class CycleController {

    private final CycleService cycleService;
    private final FormateurService formateurService;
    private final SalleRepository salleRepository;
    private final InscriptionRepository inscriptionRepository;

    public CycleController(CycleService cycleService, FormateurService formateurService, SalleRepository salleRepository , InscriptionRepository inscriptionRepository ) {
        this.cycleService = cycleService;
        this.formateurService = formateurService;
        this.salleRepository = salleRepository;
        this.inscriptionRepository = inscriptionRepository;


    }

    @GetMapping
    public String liste(@RequestParam(required = false) String recherche, Model model) {
        var cycles = cycleService.listerTous();
        if (recherche != null && !recherche.isBlank()) {
            cycles = cycles.stream()
                    .filter(c -> c.getTheme().toLowerCase().contains(recherche.toLowerCase()))
                    .toList();
        }
        model.addAttribute("cycles", cycles);
        model.addAttribute("recherche", recherche);
        return "cycle/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireAjout(Model model) {
        model.addAttribute("cycle", new CycleDTO());
        model.addAttribute("formateurs", formateurService.listerTous());
        model.addAttribute("salles", salleRepository.findAll());
        return "cycle/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("cycle") CycleDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("formateurs", formateurService.listerTous());
            model.addAttribute("salles", salleRepository.findAll());
            return "cycle/formulaire";
        }
        cycleService.creer(dto);
        return "redirect:/cycles";
    }
    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("cycle", cycleService.trouverParId(id));
        model.addAttribute("formateurs", formateurService.listerTous());
        model.addAttribute("salles", salleRepository.findAll());
        model.addAttribute("modeModification", true);

        var participantsDuCycle = inscriptionRepository.findByCycle_IdAndStatut(id, StatutInscription.ACTIVE).stream()
                .map(i -> i.getParticipant())
                .toList();
        model.addAttribute("participantsDuCycle", participantsDuCycle);

        return "cycle/formulaire";
    }

    @PostMapping("/modifier/{id}")
    public String modifier(@PathVariable Long id, @Valid @ModelAttribute("cycle") CycleDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("formateurs", formateurService.listerTous());
            model.addAttribute("salles", salleRepository.findAll());
            model.addAttribute("modeModification", true);
            return "cycle/formulaire";
        }
        cycleService.modifier(id, dto);
        return "redirect:/cycles";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        cycleService.supprimer(id);
        return "redirect:/cycles";
    }
}