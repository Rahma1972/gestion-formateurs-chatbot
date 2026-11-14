package tn.enicarthage.gestionformation.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.gestionformation.common.enums.Role;
import tn.enicarthage.gestionformation.cycle.entity.Cycle;
import tn.enicarthage.gestionformation.cycle.repository.CycleRepository;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.participant.entity.Participant;
import tn.enicarthage.gestionformation.participant.repository.ParticipantRepository;
import tn.enicarthage.gestionformation.salle.entity.Salle;
import tn.enicarthage.gestionformation.salle.repository.SalleRepository;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.inscription.entity.Inscription;
import tn.enicarthage.gestionformation.inscription.repository.InscriptionRepository;
import tn.enicarthage.gestionformation.common.enums.StatutInscription;

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
                                             PasswordEncoder encoder) {
        return args -> {
            if (formateurRepository.count() > 0) {
                return; 
            }

            // ---- Formateurs ----
            Formateur f1 = creerFormateur(encoder, "حبيب الدريدي", "صفقات عمومية", "haicop", "haicop", "habib.dridi@cfd.tn");
            Formateur f2 = creerFormateur(encoder, "رفقة العمري", "منقولات", "daca", "المركز الوطني للإعلامية", "rafka.omri@cfd.tn");
            Formateur f3 = creerFormateur(encoder, "أحمد الشريف", "أمن المعلوماتية", "الإعلامية", "المركز الوطني للإعلامية", "ahmed.charif@cfd.tn");
            Formateur f4 = creerFormateur(encoder, "سلمى بن يوسف", "إدارة المشاريع", "التصرف", "وزارة المالية", "salma.benyoussef@cfd.tn");

            formateurRepository.saveAll(List.of(f1, f2, f3, f4));

            // ---- Salles (déjà créées par DataInitializer, on les récupère) ----
            List<Salle> salles = salleRepository.findAll();
            if (salles.size() < 5) return; // sécurité si l'ordre d'exécution changeait

            // ---- Cycles ----
            Cycle c1 = creerCycle(cycleRepository, "تونابس", LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 5), salles.get(0), Set.of(f1));
            Cycle c2 = creerCycle(cycleRepository, "أمن الشبكات المعلوماتية", LocalDate.of(2026, 4, 6), LocalDate.of(2026, 4, 9), salles.get(1), Set.of(f3));
            Cycle c3 = creerCycle(cycleRepository, "الصفقات العمومية", LocalDate.of(2026, 5, 11), LocalDate.of(2026, 5, 13), salles.get(2), Set.of(f1, f4));
            Cycle c4 = creerCycle(cycleRepository, "إدارة المشاريع الرقمية", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 4), salles.get(3), Set.of(f4));
            Cycle c5 = creerCycle(cycleRepository, "تسيير الموارد البشرية", LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 17), salles.get(4), Set.of(f2));

            cycleRepository.saveAll(List.of(c1, c2, c3, c4, c5));

            // ---- Participants (16 au total : mélange d'activés et en attente) ----
            List<Participant> participants = List.of(
                    creerParticipant(encoder, "محمد علي", "09832562", "المركز الوطني للإعلامية", "71783055", "71781862", "99702895", "msaadaoui@cni.tn", true),
                    creerParticipant(encoder, "فاطمة الزهراء", "07123456", "وزارة التربية", "71222333", "", "98123456", "fatma.zahra@edu.tn", true),
                    creerParticipant(encoder, "كريم بوعزيزي", "05987654", "STEG", "71444555", "71444556", "97456789", "karim.bouazizi@steg.tn", true),
                    creerParticipant(encoder, "ليلى مبروك", "08765432", "البريد التونسي", "71666777", "", "96789123", "leila.mabrouk@poste.tn", false),
                    creerParticipant(encoder, "يوسف الحداد", "06345678", "STIR", "71888999", "", "95321654", "youssef.haddad@stir.tn", false),
                    creerParticipant(encoder, "أمال الطرابلسي", "04567891", "CNI", "71999888", "", "94654321", "amel.trabelsi@cni.tn", false),
                    creerParticipant(encoder, "نور الدين الجملي", "01122334", "وزارة الصحة", "71100200", "", "93112233", "nour.jemli@sante.tn", true),
                    creerParticipant(encoder, "هيفاء المنصوري", "02233445", "البنك المركزي", "71200300", "71200301", "92223344", "haifa.mansouri@bct.tn", true),
                    creerParticipant(encoder, "طارق الغربي", "03344556", "توتال إنيرجي", "71300400", "", "91334455", "tarek.gharbi@total.tn", true),
                    creerParticipant(encoder, "رانيا القروي", "04455667", "أوريدو تونس", "71400500", "", "90445566", "rania.karoui@ooredoo.tn", true),
                    creerParticipant(encoder, "وليد بن سالم", "05566778", "المدرسة الوطنية للمهندسين", "71500600", "71500601", "99556677", "walid.bensalem@enis.tn", false),
                    creerParticipant(encoder, "إيناس الفقيه", "06677889", "اتصالات تونس", "71600700", "", "98667788", "ines.fakih@tt.tn", false),
                    creerParticipant(encoder, "مراد الشابي", "07788990", "الديوانة التونسية", "71700800", "", "97778899", "mourad.chebbi@douane.tn", false),
                    creerParticipant(encoder, "دنيا بن حسين", "08899001", "STEG", "71800900", "71800901", "96889900", "dounia.benhssine@steg.tn", true),
                    creerParticipant(encoder, "زياد الجبالي", "09900112", "شركة الخطوط التونسية", "71900100", "", "95990011", "ziad.jebali@tunisair.tn", true),
                    creerParticipant(encoder, "سامية العيادي", "00011223", "المركز الوطني للإعلامية", "71000200", "", "94001122", "samia.ayadi@cni.tn", false)
            );

            participantRepository.saveAll(participants);
            List<Cycle> cyclesListe = List.of(c1, c2, c3, c4, c5);
            List<Cycle> cyclePourChaqueParticipant = List.of(c1, c2, c3, c4, c5, c1, c2, c3, c4, c5, c1, c2, c3, c4, c5, c1);

            for (int i = 0; i < participants.size(); i++) {
                Inscription inscription = new Inscription();
                inscription.setParticipant(participants.get(i));
                inscription.setCycle(cyclePourChaqueParticipant.get(i));
                inscription.setStatut(StatutInscription.ACTIVE);
                inscriptionRepository.save(inscription);
            }

            System.out.println(">> Données de démonstration créées : 4 formateurs, 5 cycles, 16 participants");
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