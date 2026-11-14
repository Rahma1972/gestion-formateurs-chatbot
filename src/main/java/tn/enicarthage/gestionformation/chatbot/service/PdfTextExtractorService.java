package tn.enicarthage.gestionformation.chatbot.service;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextExtractorService {

    public String extraireTexte(MultipartFile fichier) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(fichier.getInputStream());
            StringBuilder texte = new StringBuilder();
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                texte.append(new PdfTextExtractor(reader).getTextFromPage(page)).append("\n");
            }
            return texte.toString();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire le fichier PDF", e);
        } finally {
            if (reader != null) reader.close();
        }
    }
}