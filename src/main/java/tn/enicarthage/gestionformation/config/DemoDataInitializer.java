package tn.enicarthage.gestionformation.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.gestionformation.common.enums.Role;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.inscription.entity.Inscription;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.salle.entity.Salle;
import tn.enicarthage.gestionformation.salle.repository.SalleRepository;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Configuration
public class DemoDataInitializer {

    @Bean
    public CommandLineRunner initDonneesDemo(FormateurRepository formateurRepository,
                                             CycleRepository cycleRepository,
                                             ParticipantRepository participantRepository,
                                             SalleRepository salleRepository,
                                             InscriptionRepository inscriptionRepository,
                                             UtilisateurRepository utilisateurRepository,
                                             PasswordEncoder encoder) {
        return args -> {

            if (utilisateurRepository.findByEmail("admin@cfd.tn").isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setEmail("admin@cfd.tn");
                admin.setMotDePasse(encoder.encode("Admin@2026"));
                admin.setRole(Role.ADMIN);
                admin.setActif(true);
                utilisateurRepository.save(admin);
                System.out.println(" Compte administrateur créé : admin@cfd.tn");
            }

            if (salleRepository.count() == 0) {
                for (int i = 1; i <= 5; i++) {
                    Salle salle = new Salle();
                    salle.setNom("Salle " + i);
                    salle.setCapacite(30);
                    salleRepository.save(salle);
                }
                System.out.println("5 Salles créées avec succès !");
            }

            if (formateurRepository.count() > 0) {
                return;
            }

            // ---- Formateurs ----
            Formateur f1 = creerFormateur(encoder, "formateur1", "Développement Spring", "Informatique", "A", "formateur01@example.tn");
            Formateur f2 = creerFormateur(encoder, "formateur2", "Développement Angular", "Informatique", "B", "formateur02@example.tn");
            Formateur f3 = creerFormateur(encoder, "formateur3", "Développement Mobile", "Informatique", "C", "formateur03@example.tn");
            Formateur f4 = creerFormateur(encoder, "formateur4", "Management de projet", "finance", "D", "formateur04@example.tn");

            formateurRepository.saveAll(List.of(f1, f2, f3, f4));

            // ---- Récupération des Salles ----
            List<Salle> salles = salleRepository.findAll();
            if (salles.size() < 5) return;

            // ---- Cycles ----
            Cycle c1 = creerCycle(cycleRepository, "Tunaps", LocalDate.of(2026, 10, 2), LocalDate.of(2026, 12, 5), salles.get(0), Set.of(f1));
            Cycle c2 = creerCycle(cycleRepository, "CyberSecurity", LocalDate.of(2026, 11, 6), LocalDate.of(2026, 12, 9), salles.get(1), Set.of(f3));
            Cycle c3 = creerCycle(cycleRepository, "Marchès publics", LocalDate.of(2026, 12, 11), LocalDate.of(2027, 5, 13), salles.get(2), Set.of(f1, f4));
            Cycle c4 = creerCycle(cycleRepository, "Gestion des projets numériques", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 4), salles.get(3), Set.of(f4));
            Cycle c5 = creerCycle(cycleRepository, "Techniques de recherche d'emploi", LocalDate.of(2026, 12, 15), LocalDate.of(2027, 6, 17), salles.get(4), Set.of(f2));

            cycleRepository.saveAll(List.of(c1, c2, c3, c4, c5));
            // ---- Participants ----
            List<Participant> participants = List.of(
                    creerParticipant(encoder, "Participant01", "11111111", "entreprise01", "71783055", "71781862", "99702895", "participant01@example.tn", true),
                    creerParticipant(encoder, "Participant02", "22222222", "entreprise02", "71222333", "", "98123456", "participant02@example.tn", true),
                    creerParticipant(encoder, "Participant03", "33333333", "entreprise03", "71444555", "71444556", "97456789", "participant03@example.tn", true),
                    creerParticipant(encoder, "Participant04", "44444444", "entreprise04", "71666777", "", "96789123", "participant04@example.tn", false),
                    creerParticipant(encoder, "Participant05", "55555555", "entreprise05", "71888999", "", "95321654", "participant05@example.tn", false),
                    creerParticipant(encoder, "Participant06", "66666666", "entreprise06", "71999888", "", "94654321", "participant06@example.tn", false),
                    creerParticipant(encoder, "Participant07", "77777777", "entreprise07", "71100200", "", "93112233", "participant07@example.tn", true),
                    creerParticipant(encoder, "Participant08", "88888888", "entreprise08", "71200300", "71200301", "92223344", "participant08@example.tn", true),
                    creerParticipant(encoder, "Participant08", "99999999", "entreprise09", "71300400", "", "91334455", "participant09@example.tn", true),
                    creerParticipant(encoder, "Participant09", "12345678", "entreprise10", "71400500", "", "90445566", "participant10@example.tn", true),
                    creerParticipant(encoder, "Participant10", "23456789", "entreprise11", "71500600", "71500601", "99556677", "participant11@example.tn", false),
                    creerParticipant(encoder, "Participant11", "34567890", "entreprise12", "71600700", "", "98667788", "participant12@example.tn", false),
                    creerParticipant(encoder, "Participant12", "45678901", "entreprise13", "71700800", "", "97778899", "participant13@example.tn", false),
                    creerParticipant(encoder, "Participant13", "56789012", "entreprise14", "71800900", "71800901", "96889900", "participant14@example.tn", true),
                    creerParticipant(encoder, "Participant14", "67890123", "entreprise15", "71900100", "", "95990011", "participant15@example.tn", true),
                    creerParticipant(encoder, "Participant15", "78901234", "entreprise16", "71000200", "", "94001122", "participant16@example.tn", false)
            );

            participantRepository.saveAll(participants);
            List<Cycle> cyclePourChaqueParticipant = List.of(c1, c2, c3, c4, c5, c1, c2, c3, c4, c5, c1, c2, c3, c4, c5, c1);

            for (int i = 0; i < participants.size(); i++) {
                Participant p = participants.get(i);
                Inscription inscription = new Inscription();
                inscription.setParticipant(p);
                inscription.setCycle(cyclePourChaqueParticipant.get(i));
                inscription.setStatut(p.getCompte().isActif() ? StatutInscription.ACTIVE : StatutInscription.EN_ATTENTE);
                inscriptionRepository.save(inscription);
            }

            System.out.println(">> Initialisation complète réussie : Admin, 5 salles, 4 formateurs, 5 cycles, 16 participants.");
        };
    }

    private Formateur creerFormateur(PasswordEncoder encoder, String nomPrenom, String specialite, String direction, String entreprise, String email) {
        Utilisateur compte = new Utilisateur();
        compte.setEmail(email);
        compte.setMotDePasse(encoder.encode("Formateur@2026"));
        compte.setRole(Role.FORMATEUR);
        compte.setActif(true);

        Formateur formateur = new Formateur();
        formateur.setNomPrenom(nomPrenom);
        formateur.setSpecialite(specialite);
        formateur.setDirection(direction);
        formateur.setEntreprise(entreprise);
        formateur.setCompte(compte);
        return formateur;
    }

    private Cycle creerCycle(CycleRepository cycleRepository, String theme, LocalDate debut, LocalDate fin, Salle salle, Set<Formateur> formateurs) {
        Cycle cycle = new Cycle();
        long compteur = cycleRepository.count() + 1;
        cycle.setNumAct(compteur + "/" + LocalDate.now().getYear());
        cycle.setTheme(theme);
        cycle.setDateDeb(debut);
        cycle.setDateFin(fin);
        cycle.setSalle(salle);
        cycle.setFormateurs(formateurs);
        return cycle;
    }

    private Participant creerParticipant(PasswordEncoder encoder, String nomPrenom, String cin, String entreprise,
                                         String telFix, String fax, String telPort, String email, boolean actif) {
        Utilisateur compte = new Utilisateur();
        compte.setEmail(email);
        compte.setMotDePasse(encoder.encode("Participant@2026"));
        compte.setRole(Role.PARTICIPANT);
        compte.setActif(actif);

        Participant participant = new Participant();
        participant.setNomPrenom(nomPrenom);
        participant.setCin(cin);
        participant.setEntreprise(entreprise);
        participant.setTelFix(telFix);
        participant.setFax(fax);
        participant.setTelPort(telPort);
        participant.setMail(email);
        participant.setCompte(compte);
        return participant;
    }
}