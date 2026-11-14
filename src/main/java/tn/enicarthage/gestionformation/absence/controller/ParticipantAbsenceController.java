package tn.enicarthage.gestionformation.absence.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.gestionformation.absence.entity.JustificatifAbsence;
import tn.enicarthage.gestionformation.absence.service.FileStorageService;
import tn.enicarthage.gestionformation.absence.service.JustificatifService;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.security.CustomUserDetails;

@Controller
@RequestMapping("/mes-absences")
public class ParticipantAbsenceController {

    private final JustificatifService justificatifService;
    private final ParticipantRepository participantRepository;
    private final FileStorageService fileStorageService;

    public ParticipantAbsenceController(JustificatifService justificatifService,
                                        ParticipantRepository participantRepository,
                                        FileStorageService fileStorageService) {
        this.justificatifService = justificatifService;
        this.participantRepository = participantRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String mesAbsences(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        var participant = participantRepository.findAll().stream()
                .filter(p -> p.getCompte() != null && p.getCompte().getEmail().equals(userDetails.getUsername()))
                .findFirst().orElseThrow();

        model.addAttribute("absencesAJustifier", justificatifService.listerAbsencesAJustifier(participant.getId()));
        model.addAttribute("justificatifs", justificatifService.listerPourParticipant(participant.getId()));
        return "absence/mes-absences";
    }

    @PostMapping("/deposer")
    public String deposer(@RequestParam Long presenceSeanceId,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) MultipartFile fichier) {
        justificatifService.deposer(presenceSeanceId, description, fichier);
        return "redirect:/mes-absences";
    }

    @GetMapping("/fichier/{justificatifId}")
    @ResponseBody
    public ResponseEntity<Resource> telechargerFichier(@PathVariable Long justificatifId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        JustificatifAbsence justificatif = justificatifService.trouverParId(justificatifId);

        boolean estAdmin = userDetails.getUtilisateur().getRole().name().equals("ADMIN");
        boolean estProprietaire = justificatif.getPresenceSeance().getParticipant().getCompte() != null
                && justificatif.getPresenceSeance().getParticipant().getCompte().getEmail().equals(userDetails.getUsername());

        if (!estAdmin && !estProprietaire) {
            throw new AccessDeniedException("erreur.acces.justificatif");
        }

        Resource fichier = fileStorageService.charger(justificatif.getCheminFichier());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + justificatif.getNomFichier() + "\"")
                .body(fichier);
    }
}