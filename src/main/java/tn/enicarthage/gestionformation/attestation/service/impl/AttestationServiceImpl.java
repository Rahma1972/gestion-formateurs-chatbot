package tn.enicarthage.gestionformation.attestation.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import tn.enicarthage.gestionformation.attestation.service.AttestationService;
import tn.enicarthage.gestionformation.common.exception.BusinessException;
import tn.enicarthage.gestionformation.common.exception.ResourceNotFoundException;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.presence.entity.PresenceSeance;
import tn.enicarthage.gestionformation.presence.repository.PresenceSeanceRepository;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttestationServiceImpl implements AttestationService {

    private static final double SEUIL_ABSENCES_MAX = 0.20;

    private final ParticipantRepository participantRepository;
    private final CycleRepository cycleRepository;
    private final PresenceSeanceRepository presenceSeanceRepository;

    public AttestationServiceImpl(ParticipantRepository participantRepository, CycleRepository cycleRepository,
                                  PresenceSeanceRepository presenceSeanceRepository) {
        this.participantRepository = participantRepository;
        this.cycleRepository = cycleRepository;
        this.presenceSeanceRepository = presenceSeanceRepository;
    }

    @Override
    public boolean estEligible(Long participantId, Long cycleId) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        if (cycle.getDateFin().isAfter(LocalDate.now())) {
            return false;
        }

        List<PresenceSeance> seancesParticipant = presenceSeanceRepository.findByParticipant_IdAndCycle_Id(participantId, cycleId);

        if (seancesParticipant.isEmpty()) {
            return true;
        }

        long totalSeances = seancesParticipant.size();
        long absencesNonJustifiees = seancesParticipant.stream()
                .filter(s -> !s.isPresent() && !s.isJustifie())
                .count();

        double tauxAbsences = (double) absencesNonJustifiees / totalSeances;
        return tauxAbsences <= SEUIL_ABSENCES_MAX;
    }

    @Override
    public byte[] genererAttestation(Long participantId, Long cycleId) {
        if (!estEligible(participantId, cycleId)) {
            throw new BusinessException("erreur.attestation.nonEligible");
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.participant.introuvable", participantId));
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("erreur.cycle.introuvable", cycleId));

        try {
            ByteArrayOutputStream sortie = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 50, 50, 80, 50);
            PdfWriter.getInstance(document, sortie);
            document.open();

            // Utilisation de la police standard Helvetica (pour un rendu 100% français/latin)
            Font policeTitre = new Font(Font.HELVETICA, 26, Font.BOLD, Color.decode("#0E3D3E"));
            Font policeSousTitre = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.decode("#7A8683"));
            Font policeCorps = new Font(Font.HELVETICA, 16, Font.NORMAL, Color.BLACK);
            Font policeNom = new Font(Font.HELVETICA, 22, Font.BOLD, Color.decode("#0E3D3E"));

            Paragraph institution = new Paragraph("Centre National de l'Informatique", policeSousTitre);
            institution.setAlignment(Element.ALIGN_CENTER);
            document.add(institution);

            document.add(new Paragraph(" "));

            Paragraph titre = new Paragraph("ATTESTATION DE FORMATION", policeTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Paragraph corpsIntro = new Paragraph("Le Centre National de l'Informatique certifie que", policeCorps);
            corpsIntro.setAlignment(Element.ALIGN_CENTER);
            document.add(corpsIntro);

            document.add(new Paragraph(" "));

            // Récupération du nom et traitement pour toujours afficher du français/latin
            String nomParticipant = participant.getNomPrenom();

            // Si le nom contient des caractères arabes ou est vide, on le formate proprement en français
            if (nomParticipant == null || nomParticipant.isBlank() || contientArabe(nomParticipant)) {
                if (participant.getCompte() != null && participant.getCompte().getEmail() != null) {
                    // Extrait la partie avant @ de l'email pour un nom lisible en français (ex: "mohamed.ali")
                    String emailPrefix = participant.getCompte().getEmail().split("@")[0].replace(".", " ");
                    nomParticipant = capitaliser(emailPrefix);
                } else {
                    nomParticipant = "Participant N° " + participant.getId();
                }
            }

            Paragraph nom = new Paragraph(nomParticipant, policeNom);
            nom.setAlignment(Element.ALIGN_CENTER);
            document.add(nom);

            document.add(new Paragraph(" "));

            String formatterDates = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(cycle.getDateDeb())
                    + " - " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(cycle.getDateFin());

            Paragraph corpsSuite = new Paragraph(
                    "a suivi avec succès la formation intitulée « " + cycle.getTheme() + " »\ndu " + formatterDates,
                    policeCorps);
            corpsSuite.setAlignment(Element.ALIGN_CENTER);
            document.add(corpsSuite);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Paragraph date = new Paragraph("Fait le " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()), policeSousTitre);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            document.close();
            return sortie.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    // Détecte si le texte contient des caractères arabes
    private boolean contientArabe(String texte) {
        return texte.matches(".*[\\u0600-\\u06FF].*");
    }

    // Met la première lettre en majuscule (ex: "mohamed ali" -> "Mohamed Ali")
    private String capitaliser(String texte) {
        String[] mots = texte.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String m : mots) {
            if (!m.isEmpty()) {
                sb.append(Character.toUpperCase(m.charAt(0)))
                        .append(m.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}