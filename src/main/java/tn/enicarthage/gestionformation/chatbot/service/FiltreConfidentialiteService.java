package tn.enicarthage.gestionformation.chatbot.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class FiltreConfidentialiteService {

    private static final Pattern MOTIF_EMAIL = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    private static final Pattern MOTIF_TELEPHONE_CIN = Pattern.compile(
            "(?<!\\d)(\\+216[\\s-]?)?\\d{8}(?!\\d)"
    );

    public String filtrer(String texte) {
        if (texte == null) return null;

        String resultat = MOTIF_EMAIL.matcher(texte).replaceAll("[بريد إلكتروني محجوب]");
        resultat = MOTIF_TELEPHONE_CIN.matcher(resultat).replaceAll("[رقم محجوب]");

        return resultat;
    }
}