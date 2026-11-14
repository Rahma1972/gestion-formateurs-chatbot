package tn.enicarthage.gestionformation.chatbot.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.gestionformation.chatbot.dto.ChatRequestDTO;
import tn.enicarthage.gestionformation.chatbot.dto.ChatResponseDTO;
import tn.enicarthage.gestionformation.chatbot.service.ChatbotService;
import tn.enicarthage.gestionformation.chatbot.service.FiltreConfidentialiteService;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.security.CustomUserDetails;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ParticipantRepository participantRepository;
    private final FiltreConfidentialiteService filtreConfidentialiteService;

    public ChatbotController(ChatbotService chatbotService, ParticipantRepository participantRepository,
                             FiltreConfidentialiteService filtreConfidentialiteService) {
        this.chatbotService = chatbotService;
        this.participantRepository = participantRepository;
        this.filtreConfidentialiteService = filtreConfidentialiteService;
    }

    @GetMapping
    public String afficherWidget() {
        return "chatbot/widget";
    }

    @PostMapping("/message")
    @ResponseBody
    public ChatResponseDTO envoyerMessage(@Valid @RequestBody ChatRequestDTO requete, BindingResult result,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (result.hasErrors()) {
            return new ChatResponseDTO("الرجاء كتابة رسالة صحيحة.");
        }

        Long participantId = participantRepository.findAll().stream()
                .filter(p -> p.getCompte() != null && p.getCompte().getEmail().equals(userDetails.getUsername()))
                .findFirst().map(p -> p.getId()).orElse(null);

        String messageFiltre = filtreConfidentialiteService.filtrer(requete.getMessage());
        String historique = requete.getHistorique() != null ? requete.getHistorique() : "";
        String reponse = chatbotService.repondre(messageFiltre, historique, participantId);
        return new ChatResponseDTO(reponse, messageFiltre);
    }

    @PostMapping("/generer-description")
    @ResponseBody
    public ChatResponseDTO genererDescription(@RequestBody tn.enicarthage.gestionformation.chatbot.dto.GenerationDescriptionDTO requete) {
        String description = chatbotService.genererDescriptionCycle(requete.getTheme(), requete.getMotsClefs());
        return new ChatResponseDTO(description);
    }
}