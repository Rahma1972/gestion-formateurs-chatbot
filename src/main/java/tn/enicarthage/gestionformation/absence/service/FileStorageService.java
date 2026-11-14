package tn.enicarthage.gestionformation.absence.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path racineStockage;
    private static final List<String> EXTENSIONS_AUTORISEES = List.of("pdf", "jpg", "jpeg", "png");

    public FileStorageService(@Value("${app.upload.dir:uploads}") String dossierUpload) {
        this.racineStockage = Paths.get(dossierUpload, "justificatifs").toAbsolutePath().normalize();
        try {
            Files.createDirectories(racineStockage);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage", e);
        }
    }

    public String stocker(MultipartFile fichier) {
        String nomOriginal = fichier.getOriginalFilename();
        String extension = (nomOriginal != null && nomOriginal.contains("."))
                ? nomOriginal.substring(nomOriginal.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!EXTENSIONS_AUTORISEES.contains(extension)) {
            throw new IllegalArgumentException("نوع الملف غير مسموح به (pdf, jpg, png فقط)");
        }

        String nomStocke = UUID.randomUUID() + "." + extension;
        try {
            Path cible = racineStockage.resolve(nomStocke);
            Files.copy(fichier.getInputStream(), cible);
            return nomStocke;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier", e);
        }
    }

    public Resource charger(String nomFichier) {
        try {
            Path chemin = racineStockage.resolve(nomFichier).normalize();
            if (!chemin.startsWith(racineStockage)) {
                throw new SecurityException("Chemin de fichier invalide");
            }
            Resource resource = new UrlResource(chemin.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new RuntimeException("Fichier introuvable : " + nomFichier);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fichier introuvable : " + nomFichier, e);
        }
    }
}