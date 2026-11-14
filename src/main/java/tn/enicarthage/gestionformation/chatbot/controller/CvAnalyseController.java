package tn.enicarthage.gestionformation.chatbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.gestionformation.chatbot.service.ChatbotService;
import tn.enicarthage.gestionformation.chatbot.service.PdfTextExtractorService;
import tn.enicarthage.gestionformation.common.exception.BusinessException;

@Controller
@RequestMapping("/chatbot/analyser-cv")
public class CvAnalyseController {

    private final ChatbotService chatbotService;
    private final PdfTextExtractorService pdfTextExtractorService;

    public CvAnalyseController(ChatbotService chatbotService, PdfTextExtractorService pdfTextExtractorService) {
        this.chatbotService = chatbotService;
        this.pdfTextExtractorService = pdfTextExtractorService;
    }

    @GetMapping
    public String formulaire() {
        return "chatbot/analyser-cv";
    }

    @PostMapping
    public String analyser(@RequestParam(required = false) MultipartFile fichier,
                           @RequestParam(required = false) String texteCv,
                           @RequestParam String langue,
                           Model model) {
        String texte;
        if (fichier != null && !fichier.isEmpty()) {
            if (!"application/pdf".equals(fichier.getContentType())) {
                throw new BusinessException("erreur.cv.formatInvalide");
            }
            texte = pdfTextExtractorService.extraireTexte(fichier);
        } else if (texteCv != null && !texteCv.isBlank()) {
            texte = texteCv;
        } else {
            throw new BusinessException("erreur.cv.vide");
        }

        String reponse = chatbotService.analyserCv(texte, langue);
        model.addAttribute("resultat", reponse);
        return "chatbot/analyser-cv";
    }
}