package tn.enicarthage.gestionformation.formateur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import tn.enicarthage.gestionformation.common.enums.Role;
import tn.enicarthage.gestionformation.formateur.entity.Formateur;
import tn.enicarthage.gestionformation.formateur.repository.FormateurRepository;
import tn.enicarthage.gestionformation.utilisateur.entity.Utilisateur;
import tn.enicarthage.gestionformation.utilisateur.repository.UtilisateurRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FormateurIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FormateurRepository formateurRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Formateur formateur2Enregistre;

    @BeforeEach
    void creerDonneesTest() {
        if (utilisateurRepository.findByEmail("formateur.victime@test.tn").isEmpty()) {
            Utilisateur compte1 = new Utilisateur();
            compte1.setEmail("formateur.victime@test.tn");
            compte1.setMotDePasse(passwordEncoder.encode("Test@2026"));
            compte1.setRole(Role.FORMATEUR);
            compte1.setActif(true);

            Formateur formateur1 = new Formateur();
            formateur1.setNomPrenom("Formateur Un");
            formateur1.setSpecialite("Java");
            formateur1.setDirection("DACA");
            formateur1.setEntreprise("CNI");
            formateur1.setCompte(compte1);
            formateurRepository.save(formateur1);
        }

        if (utilisateurRepository.findByEmail("formateur.cible@test.tn").isEmpty()) {
            Utilisateur compte2 = new Utilisateur();
            compte2.setEmail("formateur.cible@test.tn");
            compte2.setMotDePasse(passwordEncoder.encode("Test@2026"));
            compte2.setRole(Role.FORMATEUR);
            compte2.setActif(true);

            Formateur formateur2 = new Formateur();
            formateur2.setNomPrenom("Formateur Deux");
            formateur2.setSpecialite("Réseaux");
            formateur2.setDirection("DACA");
            formateur2.setEntreprise("CNI");
            formateur2.setCompte(compte2);
            formateur2Enregistre = formateurRepository.save(formateur2);
        } else {
            formateur2Enregistre = formateurRepository.findAll().stream()
                    .filter(f -> f.getCompte().getEmail().equals("formateur.cible@test.tn"))
                    .findFirst().orElseThrow();
        }
    }

    @Test
    @WithUserDetails(value = "admin@cfd.tn", userDetailsServiceBeanName = "customUserDetailsService")
    void creerFormateur_devraitPersisterEnBaseAvecMotDePasseHache() throws Exception {
        long nombreAvant = formateurRepository.count();
        String emailUnique = "test.integration." + System.currentTimeMillis() + "@cfd.tn";

        mockMvc.perform(post("/formateurs/enregistrer")
                        .with(csrf())
                        .param("nomPrenom", "Test Intégration")
                        .param("specialite", "Java")
                        .param("direction", "DACA")
                        .param("entreprise", "CNI")
                        .param("email", emailUnique)
                        .param("motDePasse", "Test@2026"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formateurs"));

        long nombreApres = formateurRepository.count();
        assertEquals(nombreAvant + 1, nombreApres);

        var compte = utilisateurRepository.findByEmail(emailUnique);
        assertTrue(compte.isPresent());
        assertNotEquals("Test@2026", compte.get().getMotDePasse(), "Le mot de passe ne doit jamais être stocké en clair");
        assertTrue(compte.get().getMotDePasse().startsWith("$2a$") || compte.get().getMotDePasse().startsWith("$2b$"),
                "Le mot de passe doit être un hash BCrypt");
    }

    @Test
    void accesSansAuthentification_devraitEtreRedirigeVersLogin() throws Exception {
        mockMvc.perform(get("/formateurs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithUserDetails(value = "formateur.victime@test.tn", userDetailsServiceBeanName = "customUserDetailsService")
    void formateur_neDoitPasPouvoirModifierUnAutreFormateur() throws Exception {
        mockMvc.perform(get("/formateurs/modifier/" + formateur2Enregistre.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("error/erreur-metier"));
    }
}